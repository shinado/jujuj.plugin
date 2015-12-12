import org.apache.xerces.dom.AttributeMap;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by Administrator on 2015/11/18.
 */
public class LayoutHandler {

    /**
     *
     * @param layoutUrl physical url of layout
     * @param requestUrl request url
     *                   if url != null, this entity is a downloadable
     *                   else it's a nested entity
     *
     * @return
     */
    public static String generateBeanFormLayout(String layoutUrl, String className, String requestUrl){
        Document doc = XmlUtil.getDocument(layoutUrl);

        NodeList nodeList = doc.getChildNodes();
        String fields = findFiled(nodeList, getLayoutDirectory(layoutUrl));

        String layout = getLayoutNameByUrl(layoutUrl);

        StringBuilder sb = new StringBuilder("");

        sb.append("\n");
        String classAnnotation = "";
        String implementation = "";
        //Downloadable
        if (requestUrl != null){
            classAnnotation = "@ActivityInj(R.layout." + layout + ")";
            implementation = " implements Downloadable";
        }
        //nested entity
        else{
            classAnnotation = "@GroupViewInj(R.layout." + layout + ")";
        }
        sb.append(classAnnotation);

        String classDeclaration = "public class " + className + implementation + "{\n";
        sb.append("\n");
        sb.append(classDeclaration);

        sb.append(fields);

        if (requestUrl != null){
            sb.append(generateMethods(requestUrl));
        }

        sb.append("}");

        return sb.toString();
    }

    /**
     *
     * @param des the destination url
     * @param layoutUrl the url of layout
     * @return the class name of bean
     */
    public static String writeToBeanClass(String applicationPackage, String des, String layoutUrl, String requestUrl){
        String packageName = ActivityGenerator.getPackage(des);
        String layout = getLayoutNameByUrl(layoutUrl);
        String className = getClassName(layout);

        StringBuilder sb = new StringBuilder();
        sb.append(generatePackageAndImport(packageName, applicationPackage));

        String classDeclaration = generateBeanFormLayout(layoutUrl, className, requestUrl);
        sb.append(classDeclaration);

        des = des + "/" + className + ".java";
        FileIO.write(des, sb.toString());
        return className;
    }

    private static String generatePackageAndImport(String packageName, String applicationPackage){
        StringBuilder sb = new StringBuilder();
        sb.append("package " + packageName + ";");
        sb.append("\n");

        sb.append("import " + applicationPackage + ".R;");
        sb.append("\n");

        sb.append("import android.content.Context;\n" +
                "import framework.inj.ActivityInj;\n" +
                "import framework.inj.ViewInj;\n" +
                "import framework.inj.GroupViewInj;\n" +
                "import framework.inj.entity.Downloadable;\n" +
                "import java.util.List;");
        sb.append("\n");
        return sb.toString();
    }

    /**
     * @param url physical or virtual url
     * @return layout with .xml
     */
    public static String getLayoutByUrl(String url){
        int index = url.lastIndexOf("/");
        String layout = url.substring(index + 1);
        return layout;
    }

    /**
     *
     * @param url
     * @return layout name without .xml
     */
    public static String getLayoutNameByUrl(String url){
        String layout = getLayoutByUrl(url);
        return layout.replace(".xml", "");
    }

    /**
     * get standard class name like "layout_user" => LayoutUser
     * @param name of the layout without .xml
     * @return
     */
    private static String getClassName(String name){
        int length = name.length();
        StringBuilder sb = new StringBuilder();
        boolean needUpperCase = true;
        for (int i=0; i<length; i++){
            char c = name.charAt(i);
            if (c == '_'){
                needUpperCase = true;
                continue;
            }
            if (needUpperCase){
                sb.append((c+"").toUpperCase());
                needUpperCase = false;
                continue;
            }
            sb.append(c);

        }
        return sb.toString();
    }

    private static String generateMethods(String requestUrl){
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append("    @Override");
        sb.append("\n");
        sb.append("    public String onDownLoadUrl(Context context) {");
        sb.append("\n");
        sb.append("return " + "\"" + requestUrl + "\"; \n" + "    }");
        sb.append("\n");

        sb.append("\n");
        sb.append("    @Override");
        sb.append("\n");
        sb.append("    public void onDownLoadResponse(Context context) {}");
        sb.append("\n");

        sb.append("\n");
        sb.append("    @Override");
        sb.append("\n");
        sb.append("    public Object onDownloadParams() {");
        sb.append("\n");
        sb.append("        return null;\n    }");

        sb.append("\n");
        sb.append("    @Override");
        sb.append("\n");
        sb.append("    public void onError(Context context, String msg) {}");
        sb.append("\n");

        return sb.toString();

    }

    private static String findFiled(NodeList nodeList, String layoutDirectory){
        StringBuilder sb = new StringBuilder();

        int length = nodeList.getLength();
        if (length > 0){
            Node node = nodeList.item(0);
            while (node != null){
                NodeList children = node.getChildNodes();
                if (children != null && children.getLength() > 0){
                    sb.append(findFiled(children, layoutDirectory));
                }

                NamedNodeMap map = node.getAttributes();
                if (map != null){
                    Node iNode = map.getNamedItem("android:id");
                    if (iNode != null){
                        String id = iNode.getNodeValue().replace("@+id/", "");
                        String nodeName = node.getNodeName();

                        String annotationDeclaration = "    @ViewInj";
                        String filedDeclaration = "    public ";
                        if (nodeName.equals("ListView") || nodeName.equals("GridView") || nodeName.contains("RecyclerView")) {
                            Node tNode = map.getNamedItem("android:tag");
                            String nestLayout = tNode.getNodeValue();
                            String className = getClassName(nestLayout);
                            String nestLayoutUrl = layoutDirectory + "/" + nestLayout + ".xml";

                            sb.append("\n");
                            sb.append(generateBeanFormLayout(nestLayoutUrl, className, null));
                            sb.append("\n");

                            filedDeclaration += "List<" + className + "> ";
                        }else if (nodeName.equals("CheckBox")){
                            filedDeclaration += "boolean ";
                        }else {
                            filedDeclaration += "String ";
                        }
                        sb.append("\n");
                        sb.append(annotationDeclaration);
                        sb.append("\n");
                        sb.append(filedDeclaration + id + ";");
                        sb.append("\n");
                    }
                }


                node = node.getNextSibling();
            }
        }

        return sb.toString();
    }

    private static String getLayoutDirectory(String url){
        int index = url.lastIndexOf("/");
        return url.substring(0, index);
    }
}
