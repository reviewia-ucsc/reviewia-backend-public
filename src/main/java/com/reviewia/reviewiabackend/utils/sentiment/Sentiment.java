package com.reviewia.reviewiabackend.utils.sentiment;

import com.vader.sentiment.analyzer.SentimentAnalyzer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

/*
    vader:  positive sentiment: score >= 0.05
            neutral sentiment: (score > -0.05) and (score < 0.05)
            negative sentiment: score <= -0.05

    likert: positive sentiment: 4 - 5
            neutral sentiment: 3
            negative sentiment: 1 - 2
 */

public class Sentiment {
    private final float userRate;
    private float vaderRate;

    public void validate(float vader, float user) {
        if(vader >= 0.05 && user >= 4) return;
        else if(vader > -0.5 && vader < 0.05 && user == 3) return;
        else if(vader <= -0.05 && user <= 2) return;
        throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "The user rate:" + user + " and the sentiment:" + vader + " of the review do not match!");
    }

    public float analyze(String review) {
        try {
            SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(review);
            sentimentAnalyzer.analyze();
            vaderRate = sentimentAnalyzer.getPolarity().get("compound");
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Sentiment analyser failed");
        }
        validate(vaderRate, userRate);
        return (mapVaderToLikert(vaderRate, -1, 1, 1, 5, 1) + userRate) / 2;
    }

    public float mapVaderToLikert(float sourceNumber, float fromA, float fromB, float toA, float toB, int decimalPrecision ) {
        float deltaA = fromB - fromA;
        float deltaB = toB - toA;
        float scale  = deltaB / deltaA;
        float negA   = -1 * fromA;
        float offset = (negA * scale) + toA;
        float finalNumber = (sourceNumber * scale) + offset;
        int calcScale = (int) Math.pow(10, decimalPrecision);
        return (float) Math.round(finalNumber * calcScale) / calcScale;
    }

    public Sentiment(float userRate) {
        this.userRate = userRate;
    }

    public float getVaderRate() {
        return vaderRate;
    }
}


