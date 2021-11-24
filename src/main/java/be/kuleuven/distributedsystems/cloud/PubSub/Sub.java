package be.kuleuven.distributedsystems.cloud.PubSub;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.*;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PushConfig;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;

public class Sub {

    String hostport = "localhost:8083";
    ManagedChannel channel = ManagedChannelBuilder.forTarget(hostport).usePlaintext().build();
    TransportChannelProvider channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel));
    CredentialsProvider credentialsProvider = NoCredentialsProvider.create();
    TopicName topicName = TopicName.of("demo-distributed-systems-kul", "ds-cloud-topic");

    public Sub(){
        System.out.println("Started Subscriber");
        initSubscriber();
    }

    void initSubscriber(){
        try {
            SubscriptionAdminClient subscriptionClient =
                    SubscriptionAdminClient.create(
                            SubscriptionAdminSettings.newBuilder()
                                    .setTransportChannelProvider(channelProvider)
                                    .setCredentialsProvider(credentialsProvider)
                                    .build());
            ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of("demo-distributed-systems-kul", "ds-cloud-topic");
            PushConfig pushConfig = PushConfig.newBuilder()
                                        .setPushEndpoint("http://localhost:8080/pubsub/subscription")
                                        .build();

            subscriptionClient.deleteSubscription(subscriptionName);
            Subscription subscription = subscriptionClient.createSubscription(subscriptionName, topicName, pushConfig, 10);
            System.out.println("created Sub");
            System.out.println("Created push subscription: " + subscription.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
