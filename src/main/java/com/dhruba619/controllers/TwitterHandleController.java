package com.dhruba619.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Role;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("twitter")
@RestController
public class TwitterHandleController {

    @Value("${twitter.app-id}")
    String consumerKey;

    @Value("${twitter.app-secret}")
    String consumerSecret;

    @Secured({"ROLE_USER"})
    @RequestMapping(path = "authorise/url", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public String requestToken() {
        /*  TwitterTemplate twitterTemplate 
            = new TwitterTemplate(tweet.getConsumerKey(), tweet.getConsumerSecret(), tweet.getAccessToken(), tweet.getAccessTokenSecret());
        
        twitterTemplate.timelineOperations()
            .updateStatus(tweet.getTweetText());*/

        TwitterConnectionFactory connectionFactory = new TwitterConnectionFactory(consumerKey, consumerSecret);
        OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuthToken requestToken = oauthOperations.fetchRequestToken("http://127.0.0.1:8080/twitter/access/token", null);
        String authorizeUrl = oauthOperations.buildAuthorizeUrl(requestToken.getValue(), OAuth1Parameters.NONE);

        System.setProperty("requestToken", requestToken.getValue());// Store them in database
        System.setProperty("requestTokenSecret", requestToken.getSecret());// Store them in database
        return authorizeUrl;

    }

    @RequestMapping(path = "access/token", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public String getAccessToken(@RequestParam(value = "oauth_token") String oauthToken, @RequestParam(value = "oauth_verifier") String verifier) {

        System.out.println(System.getProperty("requestToken"));
        System.out.println(System.getProperty("requestTokenSecret"));
        TwitterConnectionFactory connectionFactory = new TwitterConnectionFactory(consumerKey, consumerSecret);
        OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuthToken oToken = new OAuthToken(System.getProperty("requestToken"), System.getProperty("requestTokenSecret"));
        OAuthToken accessToken = oauthOperations.exchangeForAccessToken(new AuthorizedRequestToken(oToken, verifier), null);

        System.setProperty("accessToken", accessToken.getValue());// Store them in database
        System.setProperty("accessTokenSecret", accessToken.getSecret());// Store them in database
        return "Success";
    }

    @RequestMapping(path = "tweet", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
    public String tweet() {

        TwitterTemplate twitterTemplate = new TwitterTemplate(consumerKey, consumerSecret, System.getProperty("accessToken"), System.getProperty("accessTokenSecret"));

        twitterTemplate.timelineOperations()
            .updateStatus("hello");
        return "done";

    }

}
