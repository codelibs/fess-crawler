package org.codelibs.robot.entity;

import java.io.IOException;
import java.util.Map;

import org.codelibs.core.misc.Base64Util;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class EsAccessResultData extends AccessResultDataImpl implements ToXContent {

    public EsAccessResultData() {
        super();
    }

    public EsAccessResultData(Map<String, Object> src) {
        setTransformerName((String) src.get("transformerName"));
        setEncoding((String) src.get("encoding"));
        String dataStr = (String) src.get("data");
        if (dataStr != null) {
            setData(Base64Util.decode(dataStr));
        }
    }

    @Override
    public XContentBuilder toXContent(final XContentBuilder builder, final Params params) throws IOException {
        builder.startObject();
        if (id != null) {
            builder.field("id", id);
        }
        if (transformerName != null) {
            builder.field("transformerName", transformerName);
        }
        if (data != null) {
            builder.field("data", data);
        }
        if (encoding != null) {
            builder.field("encoding", encoding);
        }
        builder.endObject();
        return builder;
    }

}
