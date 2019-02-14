package de.zalando.zmon.dataservice.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableSet;
import de.zalando.zmon.dataservice.DataServiceMetrics;
import de.zalando.zmon.dataservice.config.DataServiceConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

public abstract class AbstractWorkResultWriter implements WorkResultWriter {
  private static final Set<String> DEFAULT_ENTITY_TAG_FIELDS =
      new HashSet<>(
          ImmutableSet.of(
              "application_id",
              "application_version",
              "stack_name",
              "stack_version",
              "application",
              "version",
              "account_alias",
              "cluster_alias",
              "alias",
              "namespace"));

  private static final Logger LOG = LoggerFactory.getLogger(AbstractWorkResultWriter.class);
  private static final String TIME_SERIES_METRICS_NAME_PREFIX = "zmon.check.id.";
  private static final String REPLACE_CHAR = "_";
  private static final Pattern INVALID_TAG_CHARS = Pattern.compile("[?@:=\\[\\]]");

  private final DataServiceConfigProperties config;
  private final DataServiceMetrics dataServiceMetrics;
  private Set<String> entityTagFields;

  public AbstractWorkResultWriter(
      DataServiceConfigProperties config, DataServiceMetrics dataServiceMetrics) {
    this.config = config;
    this.dataServiceMetrics = dataServiceMetrics;
    if (null == config.getKairosdbTagFields() || config.getKairosdbTagFields().size() == 0) {
      this.entityTagFields = DEFAULT_ENTITY_TAG_FIELDS;
    } else {
      this.entityTagFields = new HashSet<>(config.getKairosdbTagFields());
    }
  }

  @Override
  public void write(WriteData writeData) {
    if (writeData.getWorkerResultOptional().isPresent()) {
      final WorkerResult workerResult = writeData.getWorkerResultOptional().get();
      if (workerResult == null || workerResult.results == null || workerResult.results.isEmpty()) {
        LOG.warn("Received a request with invalid results: {}", workerResult);
        return;
      }
      try {
        // Translate to generic data model
        List<GenericMetrics> metrics = new ArrayList<>();
        for (CheckData cd : workerResult.results) {
          formatTimeSeriesMetrics(cd, metrics);
        }
        store(metrics);

      } catch (Exception ex) {
        if (config.isLogKairosdbErrors()) {
          LOG.error("KairosDB write path failed", ex);
        }
          dataServiceMetrics.markKairosError();
      }
    }
  }

  protected abstract void store(List<GenericMetrics> metrics);

  public void formatTimeSeriesMetrics(final CheckData checkData, List<GenericMetrics> metrics) {

    if (null == config.getKairosdbTagFields() || config.getKairosdbTagFields().size() == 0) {
      this.entityTagFields = DEFAULT_ENTITY_TAG_FIELDS;
    } else {
      this.entityTagFields = new HashSet<>(config.getKairosdbTagFields());
    }

    if (!checkData.isSampled) {
      LOG.debug("Dropping non-sampled metrics for checkid={}", checkData.checkId);
      return;
    }

    final double timeStamp = checkData.checkResult.get("ts").asDouble();
    Long timeStampLong = (long) (timeStamp * 1000L);

    GenericMetrics genericMetric =
        new GenericMetrics(String.valueOf(checkData.checkId), timeStampLong);

    Map<String, NumericNode> values = new HashMap<>();
    fillFlatValueMap(values, "", checkData.checkResult.get("value"));

    // Translate datapoints to generic format
    mapDataPoints(checkData, values, genericMetric);

    metrics.add(genericMetric);
  }

  /**
   * Flattens the JSONNode that contains a single check results into a map
   *
   * @param values
   * @param prefix
   * @param base
   */
  private void fillFlatValueMap(Map<String, NumericNode> values, String prefix, JsonNode base) {
    if (base instanceof NumericNode) {
      values.put(prefix, (NumericNode) base);
    } else if (base instanceof TextNode) {
      // try to convert string node in case it is numeric
      try {
        TextNode t = (TextNode) base;
        BigDecimal db = new BigDecimal(t.textValue());
        DecimalNode dn = new DecimalNode(db);
        values.put(prefix, dn);
      } catch (NumberFormatException ex) {
        // Ignore
      }
    } else if (base instanceof ObjectNode) {
      Iterator<String> i = base.fieldNames();
      while (i.hasNext()) {
        String k = i.next();

        if (prefix.length() == 0) {
          fillFlatValueMap(values, k, base.get(k));
        } else {
          fillFlatValueMap(values, prefix + "." + k, base.get(k));
        }
      }
    }
  }

  private void mapDataPoints(
      CheckData checkData, Map<String, NumericNode> values, GenericMetrics genericMetric) {

    for (Map.Entry<String, NumericNode> e : values.entrySet()) {
      String id;
      Long value;

      String key = e.getKey();
      String[] keyParts = key.split("\\.");
      if (keyParts.length >= 3 && "health".equals(keyParts[0]) && "200".equals(keyParts[2])) {
        // remove the 200 health check data points, with 1/sec * instances with elb checks they just
        // confuse
        continue;
      }

      // Data points id = "zmon.check.1234.cpu_latency_p99"
      if (StringUtils.hasText(key)) {
        id = TIME_SERIES_METRICS_NAME_PREFIX + genericMetric.getCheckId() + "." + key;
      } else {
        id = TIME_SERIES_METRICS_NAME_PREFIX + genericMetric.getCheckId();
      }

      value = e.getValue().asLong();

      final Map<String, String> tags = getTags(key, checkData.entityId, checkData.entity);
      if (config.getActuatorMetricChecks().contains(checkData.checkId)) {
        addActuatorMetricTags(keyParts, tags);
      }

      GenericMetrics.GenericDataPoint dataPoint =
          new GenericMetrics.GenericDataPoint(id, value, tags);
      genericMetric.getDataPoints().add(dataPoint);
    }
  }

  protected Map<String, String> getTags(String key, String entityId, Map<String, String> entity) {
    Map<String, String> tags = new HashMap<>();
    tags.put("entity", INVALID_TAG_CHARS.matcher(entityId).replaceAll(REPLACE_CHAR));

    for (String field : entityTagFields) {
      if (entity.containsKey(field)) {
        String fieldValue = entity.get(field);
        if (null != fieldValue && !"".equals(fieldValue)) {
          tags.put(field, entity.get(field));
        }
      }
    }

    if (null != key && !"".equals(key)) {
      tags.put("key", INVALID_TAG_CHARS.matcher(key).replaceAll(REPLACE_CHAR));
    }

    String metricName = extractMetricName(key);
    if (null != metricName) {
      tags.put("metric", INVALID_TAG_CHARS.matcher(metricName).replaceAll(REPLACE_CHAR));
    }

    return tags;
  }

  private static String extractMetricName(String key) {
    if (null == key || "".equals(key)) return null;
    String[] keyParts = key.split("\\.");
    String metricName = keyParts[keyParts.length - 1];
    if ("".equals(metricName)) {
      metricName = keyParts[keyParts.length - 2];
    }
    return metricName;
  }

  /**
   * handle zmon actuator metrics and extract the http status code into its own field put the first
   * character of the status code into "status group" sg, this is only for easy kairosdb query
   */
  private void addActuatorMetricTags(String[] keyParts, Map<String, String> tags) {
    if (keyParts.length >= 3) {
      final String statusCode = keyParts[keyParts.length - 2];
      tags.put("sc", statusCode);
      tags.put("sg", statusCode.substring(0, 1));

      if (keyParts.length >= 4) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < keyParts.length - 3; ++i) {
          if (i > 0) {
            b.append(".");
          }
          b.append(keyParts[i]);
        }
        tags.put("path", b.toString());
      }
    }
  }
}
