/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2014 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package dummy.standalone.example;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter for adding the cross origin header.
 * @author Jan Venstermans
 * @see http://stackoverflow.com/questions/16190699/automatically-add-header-to-every-response
 */
public class CrossOriginFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		if (request.getHeader("Access-Control-Request-Method") != null
				&& "OPTIONS".equals(request.getMethod())) {
			// CORS "pre-flight" request
			response.addHeader("Access-Control-Allow-Methods",
					"GET, POST, PUT, DELETE");
			response.addHeader("Access-Control-Allow-Headers",
					"X-Requested-With,Origin,Content-Type, Accept");
		}
		filterChain.doFilter(request, response);
	}

}