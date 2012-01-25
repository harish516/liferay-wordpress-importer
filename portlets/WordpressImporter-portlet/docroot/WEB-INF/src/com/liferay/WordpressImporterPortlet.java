/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/> 
 */

package com.liferay;

import java.io.File;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.WordpressImporterUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * @author Juan Fernández
 */
public class WordpressImporterPortlet extends MVCPortlet {
 
	public void addFile(
			ActionRequest actionRequest, ActionResponse actionResponse) 
		throws Exception {
		
		System.out.println("Starting the Wordpress importer");
						
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(
				actionRequest);

			String fileName = uploadRequest.getFileName("file");
			File file = uploadRequest.getFile("file");

			if (file == null || !fileName.endsWith(".xml")) {
				System.err.println("Woops! There was an error reading the " +
					"file. Are you sure it's an xml file generated by " +
					"Wordpress?");
				return;
			}
			
			// Process Wordpress File
			
			Map<String, Integer> results = 
				WordpressImporterUtil.processFile(file, actionRequest);
			
			// Print the result message
			
			printResultMessage(results);			 
			
			String redirect = ParamUtil.get(
					uploadRequest, "redirect", StringPool.BLANK);
			
			if (Validator.isNotNull(redirect)) {
				actionResponse.sendRedirect(redirect);
			}
	}

	private void printResultMessage(Map<String, Integer> results) {
		
		int categoriesCount = results.get("categoriesCount");
		int entriesCount = results.get("entriesCount");
		int pagesCount = results.get("pagesCount");
		int tagsCount = results.get("tagsCount");			

		StringBuilder sb = new StringBuilder();
		sb.append("Sucessfully imported ");
		int baseLength = sb.length();
		
		if (pagesCount > 0) {
			sb.append(pagesCount);
			sb.append(" pages");
		}
		
		if (entriesCount > 0) {
			if (sb.length() > baseLength) {
				sb.append(", ");
			}
			
			sb.append(entriesCount);
			sb.append(" blog entries");
		}
		
		if (categoriesCount > 0) {
			if (sb.length() > baseLength) {
				sb.append(", ");
			}
			
			sb.append(categoriesCount);
			sb.append(" categories");
		}
		
		if (tagsCount > 0) {
			if (sb.length() > baseLength) {
				sb.append(", ");
			}
			
			sb.append(tagsCount);
			sb.append(" tags");
		}
		
		System.out.println(sb.toString());
	}
	
	private static Log _log = 
		LogFactoryUtil.getLog(WordpressImporterPortlet.class);
}