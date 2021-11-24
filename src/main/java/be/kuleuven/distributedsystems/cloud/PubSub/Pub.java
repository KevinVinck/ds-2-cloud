package be.kuleuven.distributedsystems.cloud.PubSub;

import be.kuleuven.distributedsystems.cloud.controller.AuthController;
import com.google.api.core.ApiFuture;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Pub {
    String hostport = "localhost:8083";
    ManagedChannel channel = ManagedChannelBuilder.forTarget(hostport).usePlaintext().build();
    TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
    CredentialsProvider credentialsProvider = NoCredentialsProvider.create();
    TopicName topicName = TopicName.of("demo-distributed-systems-kul", "ds-cloud-topic");
    Publisher publisher;

    public Pub(){
        System.out.println("creating Publisher");
        initPublisher();
    }


    void initPublisher() {
        try {
            TopicAdminClient topicClient =
                    TopicAdminClient.create(
                            TopicAdminSettings.newBuilder()
                                    .setTransportChannelProvider(channelProvider)
                                    .setCredentialsProvider(credentialsProvider)
                                    .build());

            try{
                topicClient.createTopic(topicName);
            } catch (AlreadyExistsException e){
                //e.printStackTrace();
            }
            this.publisher =
                    Publisher.newBuilder(topicName)
                            .setChannelProvider(channelProvider)
                            .setCredentialsProvider(credentialsProvider)
                            .build();
            System.out.println("Publisher created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public void publishMessage(String msg) {
        System.out.println("in publish message");
        ByteString data = ByteString.copyFromUtf8(msg);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                                            .setData(data)
                                            .putAttributes("user", AuthController.getUser().getEmail())
                                            .build();
        ApiFuture<String> publish = publisher.publish(pubsubMessage);

        try {
            String sent = publish.get();
            System.out.println("Message number from pubsub: " + sent);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }
}
