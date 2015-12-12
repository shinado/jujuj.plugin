import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by Administrator on 2015/11/18.
 */
public class ManifestHandler {

    public static final String APPLICATION_NAME = "jujuj.shinado.com.dependency.DefaultApplication";

    /**
     * get the package name from AndroidManifest.xml
     * @param url the physical url
     * @return package name of application
     */
    public static String getPackageFromManifest(String url){
        Document doc = XmlUtil.getDocument(url);
        if (doc == null){
            return "";
        }

        NodeList nList = doc.getElementsByTagName("manifest");
        if (nList.getLength() > 0){
            Node nNode = nList.item(0);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String aPackage= eElement.getAttribute("package");
                return aPackage;
            }
        }
        return "";
    }

    /**
     *
     * @param url the physical url of AndroidManifest
     * @param activity the name of activity, including package
     * @return
     */
    public static boolean addActivityToManifest(String url, String activity, boolean isLauncher){
        Document doc = XmlUtil.getDocument(url);
        if (doc == null){
            return false;
        }

        checkApplicationName(doc);

        NodeList nodeList = doc.getElementsByTagName("application");
        if (nodeList.getLength() > 0){
            Node node = nodeList.item(0);

            //new activity
            Element item = doc.createElement("activity");
            item.setAttribute("android:name", activity);

            if (isLauncher){
                Element filter = getLauncherIntentFilter(doc);
                item.appendChild(filter);
            }

            node.appendChild(item);
        }

        return XmlUtil.save(doc, url);
    }

    private static void checkApplicationName(Document doc){
        NodeList nList = doc.getElementsByTagName("application");
        if (nList.getLength() > 0){
            Node nNode = nList.item(0);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String name = eElement.getAttribute("android:name");
                //TODO only missing name considered
                if (name == null || name.equals("")){
                    eElement.setAttribute("android:name", APPLICATION_NAME);
                    return;
                }

            }
        }

    }

    private static Element getLauncherIntentFilter(Document doc){
        Element filter = doc.createElement("intent-filter");

        Element action = doc.createElement("action");
        action.setAttribute("android:name", "android.intent.action.MAIN");

        Element category = doc.createElement("category");
        category.setAttribute("android:name", "android.intent.category.LAUNCHER");

        filter.appendChild(action);
        filter.appendChild(category);
        return filter;
    }
}
