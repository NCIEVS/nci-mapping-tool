package util;

import org.openrdf.rio.RDFWriter;

/**
 * Created by noverhei on 19.02.2016.
 *
 */
class DocUtils {

    private static RDFWriter writer;

    static void setWriter(RDFWriter writer) {
        DocUtils.writer = writer;
    }

    static RDFWriter getWriter() {
        return writer;
    }
}
