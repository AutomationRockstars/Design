package com.automationrockstars.bmo.cache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.LastHttpContent;
import org.junit.Test;

import static com.automationrockstars.asserts.Asserts.*;
import static org.hamcrest.Matchers.*;

public class BucketTest {

    private static LastHttpContent emptyRequest = new LastHttpContent() {

        @Override
        public boolean release(int decrement) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean release() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public int refCnt() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public ByteBuf content() {
            return Unpooled.EMPTY_BUFFER;
        }

        @Override
        public DecoderResult getDecoderResult() {
            return DecoderResult.SUCCESS;
        }

        @Override
        public void setDecoderResult(DecoderResult result) {
        }

        @Override
        public HttpContent duplicate() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public HttpHeaders trailingHeaders() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LastHttpContent retain(int increment) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LastHttpContent retain() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public LastHttpContent copy() {
            // TODO Auto-generated method stub
            return null;
        }
    };

    @Test
    public void should_returnFalseOnNewRequest() {

    }

    @Test
    public void should_returnTrueOnOldRequest() {

    }

    @Test
    public void should_keepRequestForThread() {
    }

    @Test
    public void should_returnCachedRequest() {

    }

    @Test
    public void should_checkIfContentIsEmpty() {

        assertThat(Bucket.isEmpty(emptyRequest), is(true));
    }

    @Test
    public void should_cacheNewReques() {

    }

}
