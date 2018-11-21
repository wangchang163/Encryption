# Encryption
文件加密，音频加密，视频加密

步骤1：在清单文件中添加权限，在页面检测申请权限

步骤2：将需要加密的文件（音视频，其他文件）拖放至getExternalStorageDirectory路径中

步骤3：音频以及文件加密调用AESManager的encrypt，decrypt方法

步骤4：视频加密调用FileEnDecryptManager的initEncrypt，initdecrypt方法，通过SPUtil记录该文件是否加密

步骤5：可在locat查看加密解密日志记录
