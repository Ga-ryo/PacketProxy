/*
 * Copyright 2019 DeNA Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package packetproxy;

import java.net.ServerSocket;

import packetproxy.common.I18nString;
import packetproxy.http.Https;
import packetproxy.model.CAs.CA;
import packetproxy.model.ListenPort;
import packetproxy.util.PacketProxyUtility;

public class ProxyFactory {

	public static Proxy create(ListenPort listen_info) throws Exception {
		Proxy proxy = null;
		if (listen_info.getType() == ListenPort.TYPE.HTTP_PROXY) {
			ServerSocket listen_socket = new ServerSocket(listen_info.getPort());
			proxy = new ProxyHttp(listen_socket, listen_info);

		} else if (listen_info.getType() == ListenPort.TYPE.SSL_FORWARDER) {
			PacketProxyUtility.getInstance().packetProxyLog("type is SSL_FORWARDER");
			ServerSocket listen_socket = new ServerSocket(listen_info.getPort());
			proxy = new ProxySSLForward(listen_socket, listen_info);

		} else if (listen_info.getType() == ListenPort.TYPE.HTTP_TRANSPARENT_PROXY) {
			PacketProxyUtility.getInstance().packetProxyLog("type is HTTP_TRANSPARENT_PROXY");
			ServerSocket listen_socket = new ServerSocket(listen_info.getPort());
			proxy = new ProxyHttpTransparent(listen_socket, listen_info);

		} else if (listen_info.getType() == ListenPort.TYPE.SSL_TRANSPARENT_PROXY) {
			PacketProxyUtility.getInstance().packetProxyLog("type is SSL_TRANSPARENT_PROXY");
			ServerSocket listen_socket = new ServerSocket(listen_info.getPort());
			proxy = new ProxySSLTransparent(listen_socket, listen_info);
			
		} else if (listen_info.getType() == ListenPort.TYPE.UDP_FORWARDER) {
			proxy = new ProxyUDPForward(listen_info);

		} else if (listen_info.getType() == ListenPort.TYPE.XMPP_SSL_FORWARDER) {
			PacketProxyUtility.getInstance().packetProxyLog("type is XMPP_SSL_FORWARDER");
			ServerSocket listen_socket = new ServerSocket(listen_info.getPort());
			listen_socket.setReuseAddress(true);
			proxy = new ProxyXmppSSLForward(listen_socket, listen_info);

		} else { /* FORWARDER */
			ServerSocket listen_socket = new ServerSocket(listen_info.getPort());
			listen_socket.setReuseAddress(true);
			proxy = new ProxyForward(listen_socket, listen_info);
		}
		PacketProxyUtility.getInstance().packetProxyLog(I18nString.get("Start listening port %d.", listen_info.getPort()));
		return proxy;
	}
}
