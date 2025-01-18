package io.themade4.relictium.core.client.util;

public interface ExtChunkProviderClient {

    boolean needsTrackingUpdate();

    void setNeedsTrackingUpdate(boolean state);
}