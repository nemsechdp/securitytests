package com.nem.auradiagnostic.crane.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.testing.IdentityBolt;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

//Ejemplo rápido de Kafka-Storm.
//Storm utiliza el consumer de Kafka para su Spout, por lo que el mecanismo es similar al de los clientes Kafka.

public class StormKafkaSpout {

    public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException 
    {

        final TopologyBuilder builder = new TopologyBuilder();
        final Fields fields = new Fields("topic", "key", "mss");

        // Kafka
        Properties props = new Properties();
        props.put("bootstrap.servers", "sandbox-hpd.hortonworks.com:6667");
        props.put("acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.jaas.config", "com.sun.security.auth.module.NemPlateLoginModule required "
                    + "username=volcane;");

        KafkaSpoutConfig<String, String> kafkaSpoutConfig = KafkaSpoutConfig
                .builder(props.getProperty("bootstrap.servers"), "pruebaStorm")
                .setGroupId("storm")
                .setProp(props)
                .setRecordTranslator((r) -> new Values(r.topic(), r.key(), r.value()), new Fields("topic", "key", "mss"))
                .build();


        KafkaSpout<String, String> kafkaSpout = new KafkaSpout<>(kafkaSpoutConfig);

        // Identity bolt (just for testing, doing nothing)
        IdentityBolt identityBolt = new IdentityBolt(fields);

        // Kafka bolt to send data into "outputTopicStorm"
        KafkaBolt<String, String> kafkaBolt = new KafkaBolt<String, String>()
                .withProducerProperties(props)
                .withTopicSelector(new DefaultTopicSelector("outputTopicStorm"))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<String, String>());

        // Building the topology: KafkaSpout -> Identity -> KafkaBolt
        builder.setSpout("kafka-spout", kafkaSpout);
        builder.setBolt("identity", identityBolt).shuffleGrouping("kafka-nem-spout");
        builder.setBolt("kafka-nem-bolt", kafkaBolt, 2).globalGrouping("identity");

        // Submit the topology
        List<String> li=new ArrayList<String>();
        li.add("<host>");

        Config conf = new Config();
        conf.put(Config.NIMBUS_SEEDS, li);
        StormSubmitter.submitTopology("NEM-Kafka-Storm-Kafka", conf, builder.createTopology());

    }
}
