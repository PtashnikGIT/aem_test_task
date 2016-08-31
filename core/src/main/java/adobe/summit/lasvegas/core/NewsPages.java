package adobe.summit.lasvegas.core;

import com.adobe.cq.sightly.WCMUse;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewsPages extends WCMUse {

    private final List<NewsPageModel> newsPages = new ArrayList<>();

    /**
     * Method to build a list of NewsPageModel, this will be iterated to list
     * the subpages with the right properties
     *
     */
    @Override
    public void activate() {
        Iterator<Page> pageIterator = getCurrentPage().listChildren();

        // Looking through the subpages
        while (pageIterator.hasNext()) {
            Page page = pageIterator.next();
            Resource contentResource = page.getContentResource();
            if (contentResource != null) {
                NewsPageModel model = new NewsPageModel();
                model.setPath(page.getPath());
                model.setTitle(page.getTitle());

                if (!page.isHideInNav()) {

                    final Resource par = contentResource.getChild("par");
                    if (par != null) {
                        for (Resource child : par.getChildren()) {
                            if (child.isResourceType("aemcodingerrors/components/content/text")) {
                                if (model.getIntroText() == null) {
                                    model.setIntroText(child.adaptTo(ValueMap.class).get("text", String.class));
                                }
                            } else
                            if (child.isResourceType("aemcodingerrors/components/content/image")) {
                                if (model.getImagePath() == null) {
                                    model.setImagePath(child.adaptTo(ValueMap.class).get("fileReference", String.class));
                                }
                            }
                        }
                    }
                }

                newsPages.add(model);
            }
        }

    }

    public List<NewsPageModel> getNewsPages() {
        return newsPages;
    }

}
