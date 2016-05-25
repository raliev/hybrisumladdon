package com.epam.controllers.pages;

import com.epam.controllers.pages.graphviz.GraphViz;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * Created by Rauf_Aliev on 5/24/2016.
 */

@Controller
@Scope("tenant")
@RequestMapping("/showTreeGenerateGraph")
public class GenerateGraph extends AbstractPageController {

    private static final String TEMP_PATH = "c:\\temp\\graph."; //Windows

    private static final String GRAPH_TYPE_PNG = "png";
    private static final String GRAPH_TYPE_PDF = "pdf";
    private static final String GRAPH_TYPE_SVG = "svg";

    private static final String CONTENT_TYPE_PNG = "image/png";
    private static final String CONTENT_TYPE_PDF = "application/pdf";

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST } )
    public void generateGraph (@RequestParam(value = "dot", required = false, defaultValue = "") final String dot,
                           final Model model,
                           final HttpServletRequest request, final HttpServletResponse response) throws IOException  // NOSONAR
    {
        String target = request.getRequestURI();
        response.setStatus(200);

        if(StringUtils.isNotBlank(dot) && GraphViz.isValidDotText(dot)) {

            target = (StringUtils.isNotBlank(target) ?
                    StringUtils.remove((URLDecoder.decode(target, "UTF-8")).trim().toLowerCase(), '/') : null);

            FileEntity graph = this.generate(dot, target);
            IOUtils.copy(graph.getContent(), response.getOutputStream());
            //response.setContentType("image/png");
            //response.setHeader("Content-Disposition", "attachment; filename=image.png");
            response.setContentLength(safeLongToInt(graph.getContentLength()));
            response.flushBuffer();

        }
        //return response.getOutputStream();
    }

    public static int safeLongToInt(long l) {
        int i = (int)l;
        if ((long)i != l) {
            throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
        }
        return i;
    }
    private FileEntity generate(String dot, String target) {

        GraphViz gv = new GraphViz();
        gv.readString(dot);

        ContentType contentType;
        String graphType;

        if(GRAPH_TYPE_SVG.equals(target)) {

            graphType = GRAPH_TYPE_SVG;
            contentType = ContentType.APPLICATION_SVG_XML;

        } else if(GRAPH_TYPE_PDF.equals(target)) {
            graphType = GRAPH_TYPE_PDF;
            contentType = ContentType.create(CONTENT_TYPE_PDF, (Charset) null);

        } else {
            // default png
            graphType = GRAPH_TYPE_PNG;
            contentType = ContentType.create(CONTENT_TYPE_PNG, (Charset) null);
        }

        //	    String type = "dot";
        //	    String type = "fig";    // open with xfig
        //	    String type = "pdf";
        //	    String type = "ps";
        //	    String type = "svg";    // open with inkscape
        //	    String type = "png";
        //	      String type = "plain";
        File out = new File(TEMP_PATH + graphType);   // Linux

        gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), graphType ), out );

        FileEntity body = new FileEntity(out, contentType);

        return body;

    }

}
