package utilities;


import java.nio.file.FileStore;
import java.nio.file.FileSystems;
//import java.text.NumberFormat;

public class DiskFreeTest {
	public static void main(String args[]) {
//		NumberFormat nf = NumberFormat.getNumberInstance();

		//		Path root = FileSystems.getDefault().getPath("www/foo.bar");

		System.out.println("# store, total, used, available, readyOnly, name, type");

		for (FileStore store : FileSystems.getDefault().getFileStores()) {
			try { 
				long total = store.getTotalSpace() / 1024;
				long used = (store.getTotalSpace() - store.getUnallocatedSpace()) / 1024;
				long avail = store.getUsableSpace() / 1024;
				System.out.format("%-30s %12d %12d %12d %b %s %s%n", store.toString(), total, used, avail,store.isReadOnly(),store.name(),store.type());
				
				if ( store.toString().startsWith("/media/usb0") ) {
					System.out.println("# this is our USB drive");
				}
				
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}

//
//
//
//		//for (Path root : FileSystems.getDefault().getRootDirectories()) {
//		for ( int i=0 ; i<args.length ; i++ ) {
//			Path root = FileSystems.getDefault().getPath(args[i]);
//
//			System.out.print(root + " ");
//
//			try	{
//				FileStore store = Files.getFileStore(root);
//
//				long available = store.getUsableSpace();
//				long total = store.getTotalSpace();
//
//				long availableMegabytes = available / 1024;
//				long totalMegabytes = total / 1024;
//				double percentFree = ( (double) availableMegabytes / (double) totalMegabytes) * 100.0;
//
//				System.out.println("readyOnly=" + store.isReadOnly() + " available=" + availableMegabytes + " total=" + totalMegabytes + " % free=" + percentFree );
//
//
//
//
//				//				System.out.println("available=" + nf.format(store.getUsableSpace()) + ", total=" + nf.format(store.getTotalSpace()));
//			} catch (Exception e)	{
//				System.out.println("error querying space: " + e.toString());
//			}
//		}

	}
}
