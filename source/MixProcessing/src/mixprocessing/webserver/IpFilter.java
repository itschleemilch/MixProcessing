/*
 MixProcessing - Live Mixing of Processing Sketches 
 https://github.com/itschleemilch/MixProcessing

 Copyright (c) 2014 Sebastian Schleemilch

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mixprocessing.webserver;

import mixprocessing.util.SinglePreference;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Provides an easy mac filter for the webserver
 *
 * @author Sebastian Schleemilch
 */
public class IpFilter {
    final ArrayList<String> whitelist = new ArrayList<>();
    private final boolean filterOn;

    public IpFilter() {
        boolean filterOnPreference;
        String value;
        
        /* Test if filter is enabled */
        value = SinglePreference.getPreference(KEY_MACFILTER_ENABLED, "false");
        filterOnPreference = Boolean.parseBoolean(value);
        SinglePreference.setPreference(KEY_MACFILTER_ENABLED, value);
        this.filterOn = filterOnPreference;
        
        /* Load and format whitelist */
        if(filterOn) {
            String filterList = SinglePreference.getPreference(KEY_MACFILTER_WHITELIST, "");
            StringBuilder filterListOut = new StringBuilder();
            String[] filterIPs = new String[0];
            if(filterList != null && filterList.length()>0) {
                filterIPs = filterList.split("[\r\n,;]");
            }
            Arrays.sort(filterIPs);
            
            for(String filterIP : filterIPs) {
                String realFilterIP = filterIP.trim();
                if(realFilterIP.length()>0) {
                    filterListOut.append(realFilterIP).append("\r\n");
                    whitelist.add(realFilterIP);
                }
            }
            
            SinglePreference.setPreference(KEY_MACFILTER_WHITELIST, 
                        filterListOut.toString());
        }
    }
    
    public boolean isAccepted(Socket client) {
        if(filterOn) {
            InetAddress remotehost = client.getInetAddress();
            String remoteIP = remotehost.getHostAddress();
            
            /* Allow all local actions */
            if(remoteIP.equals("0:0:0:0:0:0:0:1") || remoteIP.equals("127.0.0.1")) {
                return true;
            }
            /* Access disallowed */
            else {
                
                if(whitelist.contains(remoteIP)) {
                    return true;
                }
                else {
                    System.err.println("Refused network client, IP: " + remoteIP);
                    return false;
                }
            }
        } else { // Filter is disabled
            return true;
        }
    }
    
    public static final String KEY_MACFILTER_ENABLED = "webserver.ipfilter.enabled";
    public static final String KEY_MACFILTER_WHITELIST = "webserver.ipfilter.whitelist";
}
