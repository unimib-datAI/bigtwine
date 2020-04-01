package it.unimib.disco.bigtwine.streamprocessor;

import java.io.Serializable;

enum StreamType implements Serializable {
    status, linkedTweet, resource, decodedLocation
}
