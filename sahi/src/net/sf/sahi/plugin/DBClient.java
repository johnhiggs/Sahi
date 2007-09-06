/**
 * Sahi - Web Automation and Test Tool
 *
 * Copyright  2006  V Narayan Raman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sahi.plugin;

import net.sf.sahi.request.HttpRequest;
import net.sf.sahi.response.HttpResponse;
import net.sf.sahi.response.SimpleHttpResponse;
import net.sf.sahi.util.Utils;
import net.sf.sahi.util.ClassLoadHelper;

import java.sql.*;
import java.util.*;

public class DBClient {
    public String driverName;
    public String jdbcurl;
    public String username;
    public String password;
    public String sql;

    public void execute(HttpRequest request) {
        try {
            init(request);
            ClassLoadHelper.getClass(driverName);
            Connection connection = DriverManager.getConnection(jdbcurl, username, password);
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpResponse select(HttpRequest request) {
        try {
            init(request);
            String s = getJSObject(getResult(driverName, jdbcurl, username, password));
//            System.out.println(s.replaceAll("\\},\\{", "},\n{"));
            return new SimpleHttpResponse(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    ArrayList getResult(String driverName, String jdbcurl, String username, String password) throws ClassNotFoundException, SQLException {
        ClassLoadHelper.getClass(driverName);
        Connection connection = DriverManager.getConnection(jdbcurl, username, password);
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        ArrayList columnNames = new ArrayList();
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();
        for (int i = 1; i < numColumns + 1; i++) {
            String columnName = rsmd.getColumnName(i);
            columnNames.add(columnName);
        }
        ArrayList list = new ArrayList();
        while (rs.next()) {
            HashMap map = new LinkedHashMap();
            for (Iterator iterator = columnNames.iterator(); iterator.hasNext();) {
                String columnName = (String) iterator.next();
                String value = rs.getString(columnName);
                map.put(columnName, value);
            }
            list.add(map);
        }
        return list;
    }

    String getJSObject(ArrayList list) {
        StringBuffer sb = new StringBuffer();
        sb.append("var a=[");
        boolean isFirst1 = true;
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            if (isFirst1) {
                isFirst1 = false;
            } else {
                sb.append(",");
            }
            sb.append("{");
            boolean isFirst2 = true;
            HashMap map = (HashMap) iterator.next();
            Set keys = map.keySet();
            for (Iterator iterator1 = keys.iterator(); iterator1.hasNext();) {
                if (isFirst2) {
                    isFirst2 = false;
                } else {
                    sb.append(",");
                }
                String columnName = (String) iterator1.next();
                String value = (String) map.get(columnName);
                sb.append(columnName + ":\"" + Utils.escapeDoubleQuotesAndBackSlashes(value) + "\"");
            }
            sb.append("}");
        }
        sb.append("];a");
        return sb.toString();
    }


    private void init(HttpRequest request) {
        driverName = request.getParameter("driver");
        jdbcurl = request.getParameter("jdbcurl");
        username = request.getParameter("username");
        password = request.getParameter("password");
        sql = request.getParameter("sql");
        System.out.println("Driver: " + driverName);
        System.out.println("JDBC URL: " + jdbcurl);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("SQL: " + sql);
    }
}
