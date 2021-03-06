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

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.WordpressUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.File;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

/**
 * @author Juan Fernández
 */
public class WordpressImporterPortlet extends MVCPortlet {
 
	public void importWordpressSite(
			ActionRequest actionRequest, ActionResponse actionResponse) 
		throws Exception {
		
		System.out.println("Starting the Wordpress importer");	
								
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(
			actionRequest);

			String fileName = uploadRequest.getFileName("file");
			File file = uploadRequest.getFile("file");

			// Validate the file

			if (file == null || !fileName.endsWith(".xml")) {
				SessionErrors.add(actionRequest, "invalid-file");				

				return;
			}
			
			// Process Wordpress File

			Map<String, Integer> results = 
				WordpressUtil.processFile(file, actionRequest);
		
			// Print the result message
			
			String resultMessage = printResultMessage(results);			 
			
			SessionMessages.add(actionRequest, resultMessage);
			
			sendRedirect(actionRequest, actionResponse);
	}

	private String printResultMessage(Map<String, Integer> results) {
		
		int categoriesCount = results.get("categoriesCount");
		int commentsCount = results.get("commentsCount");
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
		
		if (commentsCount > 0) {
			if (sb.length() > baseLength) {
				sb.append(", ");
			}
			
			sb.append(commentsCount);
			sb.append(" comments");
		}
		
		System.out.println(sb.toString());
		
		return sb.toString();
	}
	
}