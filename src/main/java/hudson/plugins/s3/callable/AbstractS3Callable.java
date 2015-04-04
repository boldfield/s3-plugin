package hudson.plugins.s3.callable;

import hudson.util.Secret;

import java.io.Serializable;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;

public class AbstractS3Callable implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String accessKey;
    private final Secret secretKey;
    private final String proxyHost;
    private final String proxyPort;
    private final boolean useRole;
    private transient AmazonS3Client client;

    public AbstractS3Callable(String accessKey, Secret secretKey, String proxyHost, String proxyPort, boolean useRole) 
    {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.useRole = useRole;
    }

    protected AmazonS3Client getClient() 
    {
        if (client == null) {
            if (useRole) {
                final InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider(false);
                client = new AmazonS3Client(credentialsProvider.getCredentials(), getClientConfiguration());
            } else {
                client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey.getPlainText()), getClientConfiguration());
            }
        }
        return client;
    }

    private ClientConfiguration getClientConfiguration(){
        final ClientConfiguration clientConfiguration = new ClientConfiguration();
        if(proxyHost != null && proxyHost.length() > 0) {
            clientConfiguration.setProxyHost(proxyHost);
            clientConfiguration.setProxyPort(Integer.parseInt(proxyPort));
        }
        return clientConfiguration;
    }

}
