package com.likeshare.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import com.likeshare.LoginUI;
import com.likeshare.tree.DevicesManager;

public class nioClientReadThread implements Runnable {
	private Selector selector;
	private DevicesManager dm;
	private LikeShareService lss;
	public nioClientReadThread(Selector selector,DevicesManager dm,LikeShareService lss) {
		this.selector = selector;
		this.dm = dm;
		this.lss = lss;
		new Thread(this).start();
	}

	public void run() {
		try {
			while (selector.select() > 0) {
				// �M���C�Ӧ��i��IO�ާ@Channel������SelectionKey
				for (SelectionKey sk : selector.selectedKeys()) {

					// �p�G��SelectionKey������Channel�����iŪ���ƾ�
					if (sk.isReadable()) {
						// �ϥ�NIOŪ��Channel�����ƾ�
						SocketChannel sc = (SocketChannel) sk.channel();
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						sc.read(buffer);
						buffer.flip();
						// �N�r�`��Ƭ���UTF-16���r�Ŧ�
						String receivedString = Charset.forName("UTF-16")
								.newDecoder().decode(buffer).toString();
						// ����x���L�X��
						System.out.println("������Ӧ۪A�Ⱦ�"
								+ sc.socket().getRemoteSocketAddress() + "���H��:"
								+ receivedString);

						String tmp[] = receivedString.split(",");
						switch (Integer.parseInt(tmp[0])) {
						case 0: // login
							LoginUI.login(Boolean.parseBoolean(tmp[1]));
							break;
						case 1: // ����n�ͤW�u�q�� tmp[1] = MAC
							dm.deviceLogin(tmp[1]);
							break;
						case 2: // �ШD�ڪ��Ҧ��]��
							dm.createMyDevices(receivedString);
							break; // ���� �ڪ��Ҧ��]�ƦW��
						case 3: // ���U���\���ѳq�D

							break;
						case 4: // ����n�ͲM��
							dm.createMyFriends(receivedString);
							break;
						case 5: // ����n�;֦����]��
							dm.setFriendUp(receivedString);
							break;
						case 6: // �s�����A�����w��port
							lss.startBioFile(receivedString);
							break;
						case 7: // �����U��
							lss.stopBioFileService();
							break;
						case 9:
							lss.bioVideoService(receivedString);
							break;
						}

						// ���U�@��Ū���@�ǳ�
						sk.interestOps(SelectionKey.OP_READ);
					}
					// �R�����b�B�z��SelectionKey
					selector.selectedKeys().remove(sk);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}