package ardis.control.xml;

import ardis.model.Objeto;
import ardis.model.conceitual.relacionamento.cardinalidade.MembroRelacionamento;
import ardis.model.logico.coluna.Coluna;
import ardis.model.logico.constraint.FK;
import ardis.model.logico.indice.Indice;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Xml {

    public Xml() {
        addCodecs();
    }

    public String objectToXml(mxGraph graph, String caminho) {

        mxCodec codec = new mxCodec() {
            @Override
            public String reference(Object obj) {
                String reference = null;
                if (obj instanceof Objeto) {
                    reference = ((Objeto) obj).getOriginalHash() + "";
                }
                return reference;
            }
        };

        String xml = mxXmlUtils.getXml(codec.encode(graph.getModel()));

        try {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(caminho))) {
                out.write(xml);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return xml;
    }

    private void addCodecs() {

        mxCodecRegistry.register(new mxObjectCodec(new MembroRelacionamento()) {
            protected boolean isPrimitiveValue(Object value) {
                return super.isPrimitiveValue(value) || value.getClass().isEnum();
            }

            protected void setFieldValue(Object obj, String fieldname, Object value) {
                Field field = getField(obj, fieldname);
                if (field.getType().isEnum()) {
                    Object[] c = field.getType().getEnumConstants();
                    for (int i = 0; i < c.length; i++) {
                        if (c[i].toString().equals(value)) {
                            value = c[i];
                            break;
                        }
                    }
                }
                super.setFieldValue(obj, fieldname, value);
            }
        });

        mxCodecRegistry.register(new mxObjectCodec(new Indice()) {
            protected boolean isPrimitiveValue(Object value) {
                return super.isPrimitiveValue(value) || value.getClass().isEnum();
            }

            protected void setFieldValue(Object obj, String fieldname, Object value) {
                Field field = getField(obj, fieldname);
                if (field.getType().isEnum()) {
                    Object[] c = field.getType().getEnumConstants();
                    for (int i = 0; i < c.length; i++) {
                        if (c[i].toString().equals(value)) {
                            value = c[i];
                            break;
                        }
                    }
                }
                super.setFieldValue(obj, fieldname, value);
            }
        });

        mxCodecRegistry.register(new mxObjectCodec(new FK()) {
            protected boolean isPrimitiveValue(Object value) {
                return super.isPrimitiveValue(value) || value.getClass().isEnum();
            }

            protected void setFieldValue(Object obj, String fieldname, Object value) {
                Field field = getField(obj, fieldname);
                if (field.getType().isEnum()) {
                    Object[] c = field.getType().getEnumConstants();
                    for (int i = 0; i < c.length; i++) {
                        if (c[i].toString().equals(value)) {
                            value = c[i];
                            break;
                        }
                    }
                }
                super.setFieldValue(obj, fieldname, value);
            }

            @Override
            protected void writeComplexAttribute(mxCodec enc, Object obj, String attr, Object value, Node node) {
                if (value instanceof Map) {
                    Map<Coluna, Coluna> map = (Map) value;

                    Node child = enc.getDocument().createElement("Map");
                    mxCodec.setAttribute(child, "as", attr);
                    Node keysNode = enc.getDocument().createElement("keys");
                    Node valuesNode = enc.getDocument().createElement("values");
                    for (Map.Entry<Coluna, Coluna> entry : map.entrySet()) {
                        encodeValue(enc, obj, null, entry.getKey(), keysNode);
                        encodeValue(enc, obj, null, entry.getValue(), valuesNode);
                    }

                    child.appendChild(keysNode);
                    child.appendChild(valuesNode);
                    node.appendChild(child);
                } else {
                    super.writeComplexAttribute(enc, obj, attr, value, node);
                }
            }

            @Override
            protected void decodeChild(mxCodec dec, Node child, Object obj) {
                if (child.getNodeName().equals("Map")) {
                    List<Coluna> keys = new ArrayList<>();
                    List<Coluna> values = new ArrayList<>();
                    Node keysNode = child.getFirstChild();
                    Node valuesNode = child.getLastChild();
                    decodeChildren(dec, keysNode, keys);
                    decodeChildren(dec, valuesNode, values);
                    Map<Coluna, Coluna> map = new LinkedHashMap<>();
                    for (int i = 0; i < keys.size(); i++) {
                        map.put(keys.get(i), values.get(i));
                    }

                    String fieldname = getFieldName(((Element) child).getAttribute("as"));
                    setFieldValue(obj, fieldname, map);
                } else {
                    super.decodeChild(dec, child, obj);
                }
            }
        });
    }
}
