package adobe.summit.lasvegas.core.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.common.base.Objects;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;

//
// you can call the servlet like this to test it
// http://localhost:4510/bin/vegas?path=/content/aemcodingerrors/en
//

@Component(metatype=true, policy = ConfigurationPolicy.REQUIRE)
@SlingServlet(paths="/bin/vegas", methods="GET", name="Las Vegas servlet", metatype=true, generateComponent=false)
public class VegasServlet extends SlingAllMethodsServlet {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(VegasServlet.class);

	@Property(label="Properties to be displayed", value={"jcr:title","jcr:description"})
	private static final String PROPERTY_NAMES = "propertyNames";
	
	private String[] configuredProps;
	
	@Activate
	public void activate(ComponentContext context) {
		configuredProps = (String[])  context.getProperties().get("propertyNames");

		if (configuredProps != null)
		LOGGER.info("props {}", configuredProps.length);
	}
	
	
	@Override
	protected void doGet(SlingHttpServletRequest request,
						 SlingHttpServletResponse response) throws ServletException,
			IOException {
		String path = request.getParameter("path");
		PageManager pm = request.getResourceResolver().adaptTo(PageManager.class);
		Page page = pm.getPage(path);

		if (page != null) {
			JSONObject json = new JSONObject();
			PrintWriter pw = response.getWriter();
			Map properties = page.getContentResource().adaptTo(Map.class);
			try {
				for (String configuredProp : configuredProps) {
					json.put(configuredProp, Objects.firstNonNull(properties.get(configuredProp), ""));
				}
				json.write(pw);
			} catch (JSONException e) {
				LOGGER.error(e.getMessage(), e);
				throw new ServletException(e);
			} finally {
                pw.close();
            }

		}
	}
}
