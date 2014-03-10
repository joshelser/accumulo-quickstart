//import org.apache.commons.compress.archivers.ArchiveStreamFactory
//import org.apache.commons.compress.archivers.ArchiveInputStream
//import org.apache.commons.compress.archivers.ArchiveEntry
//import org.apache.commons.compress.compressors.CompressorStreamFactory
//import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.{BufferedInputStream, BufferedOutputStream, File, FileInputStream, FileOutputStream}
import org.apache.commons.io.IOUtils

object Unpack {

  def gunzipTar(tarFile: File, dest: File): String = {
    dest.mkdir()

    val tarIn = new TarArchiveInputStream(
      new GzipCompressorInputStream(
        new BufferedInputStream(
          new FileInputStream(
            tarFile
          )
        )
      )
    )

    var tarEntry = tarIn.getNextTarEntry()

    val topDir = tarEntry.getName().split(File.separator)(0)

    while (tarEntry != null) {
      val file = new File(dest, tarEntry.getName())
      if (tarEntry.isDirectory()) {
        file.mkdirs()
      } else {
        file.getParentFile.mkdirs()
        file.createNewFile
        var btoRead = new Array[Byte](1 * 1024)
        var bout = new BufferedOutputStream(new FileOutputStream(file))
        var len = 0
        len = tarIn.read(btoRead)
        while(len != -1) {
          bout.write(btoRead,0,len)
          len = tarIn.read(btoRead)
        }
        bout.close()
        btoRead = null
      }
      tarEntry = tarIn.getNextTarEntry()
    }
    tarIn.close()

    // set bin files to executable
    val binDir = s"${topDir}${File.separator}bin"
    for(binFile <- new File(dest, binDir).listFiles) {
      // use a map here
      binFile.setExecutable(true, false)
    }

    return topDir
  }

}
