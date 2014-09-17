package dataGS;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class HTTPServerJSON extends NanoHTTPD {
	public static final boolean DEBUG=false;
	
	public static final String MIME_JAVASCRIPT = "text/javascript";
	public static final String MIME_CSS = "text/css";
	public static final String MIME_JPEG = "image/jpeg";
	public static final String MIME_GIF = "image/gif";
	public static final String MIME_PNG = "image/png";
	public static final String MIME_SVG = "image/svg+xml";
	public static final String MIME_JSON = "application/json";

	protected JSONDataLast lastData;
	protected JSONDataHistory historyData;

	public HTTPServerJSON(int port, JSONDataLast s, JSONDataHistory h) {
		super(port);

		lastData=s;
		historyData=h;
	}

	@Override public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		String uri = session.getUri();


		if ( DEBUG ) {
			System.out.println(method + " '" + uri + "' ");
			System.err.println("parms: " + session.getHeaders());
		}

		
		Response response=null;

		/* choose which document to return */
		/* file system */
		if ( uri.endsWith("favicon.ico") ) {
			try {
				response = new NanoHTTPD.Response( Response.Status.OK, MIME_GIF, new FileInputStream("www/favicon.ico"));
			} catch (FileNotFoundException e) {
				response = new NanoHTTPD.Response("{}");
			}
		/* dynamically generated JSON */
		} else if ( uri.endsWith("channels.json") ) {
			response = new NanoHTTPD.Response("{}");
		} else if ( uri.endsWith("now.json") ) {
			/* generate the response */
			response = new NanoHTTPD.Response( Response.Status.OK,MIME_JSON, lastData.getLastDataJSON() );
		} else if ( uri.endsWith("history.json") ) {
			/* generate the response */
			response = new NanoHTTPD.Response( Response.Status.OK,MIME_JSON, historyData.getHistoryDataJSON() );	
		} else {
			response = new NanoHTTPD.Response("{}");
		}
		

		/* this allows the website with the AJAX page to be on a different server than us */ 
		response.addHeader("Access-Control-Allow-Origin",session.getHeaders().get("origin"));

		return response;
	}


}
