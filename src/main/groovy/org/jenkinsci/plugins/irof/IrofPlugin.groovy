package org.jenkinsci.plugins.irof

import hudson.Plugin
import hudson.Extension
import org.kohsuke.stapler.DataBoundConstructor
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
import twitter4j.*
import twitter4j.auth.AccessToken
import java.util.logging.Logger
import java.util.logging.Level
import jenkins.model.Jenkins

class IrofPlugin extends Plugin {

    private static final Logger LOGGER = Logger.getLogger(IrofPlugin.name)

    String consumerKey
    String consumerSecret
    String accessToken
    String accessTokenSecret

    transient TwitterStreamFactory tsFactory

    @Override
    void start() {
        load()
        startStreaming()
        super.start()
    }

    @Override
    void stop() {
        tsFactory?.instance?.shutdown()
        super.stop()
    }

    @Override
    void configure(StaplerRequest req, JSONObject formData) {
        consumerKey = formData.getString("consumerKey")
        consumerSecret = formData.getString("consumerSecret")
        accessToken = formData.getString("accessToken")
        accessTokenSecret = formData.getString("accessTokenSecret")
        save()
        super.configure(req, formData)
        startStreaming()
    }

    void startStreaming() {
        tsFactory?.instance?.shutdown()
        tsFactory = new TwitterStreamFactory()
        tsFactory.instance.with {
            if (consumerKey && consumerSecret && accessToken && accessTokenSecret) {
                setOAuthConsumer(consumerKey, consumerSecret)
                setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret))
                addListener([
                    onException: { e -> LOGGER.log(Level.SEVERE, e.message, e) },
                    onStatus: { status ->
                        if (!status.retweet && status.user.id == 14969725L) {
                            Jenkins.instance.items.findResults { it.getTrigger(IrofTrigger) }*.run(status)
                        }
                    }
                ] as UserStreamAdapter)
                filter(new FilterQuery().follow(14969725L))
            }
        }
    }
}
