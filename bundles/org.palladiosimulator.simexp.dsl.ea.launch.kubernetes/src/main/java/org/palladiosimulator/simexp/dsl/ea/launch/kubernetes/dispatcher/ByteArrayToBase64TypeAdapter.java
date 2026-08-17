package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.lang.reflect.Type;
import java.util.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
    private final Base64.Decoder decoder = Base64.getDecoder();
    private final Base64.Encoder encoder = Base64.getEncoder();

    @Override
    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return decoder.decode(json.getAsString());
    }

    @Override
    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(encoder.encodeToString(src));
    }
}
