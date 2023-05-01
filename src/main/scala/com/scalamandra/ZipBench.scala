package com.scalamandra

import org.openjdk.jmh.annotations.*

import fs2.*
import fs2.io.file.*
import cats.effect.*
import cats.effect.unsafe.implicits.global

import java.nio.file.{Path as JPath, Files as JFile, FileSystem, FileSystems}

import scala.compiletime.uninitialized

object ZipBench:
  val zip: Path = Path("test.zip")

@State(Scope.Benchmark)
class ZipBench:
  import ZipBench.*

  @Param(Array("under64kb.txt", "over64kb.txt"))
  var fileName: String = uninitialized
  var zipFs: FileSystem = uninitialized
  var file: Path = uninitialized
  var lib: ZipLib[IO] = uninitialized

  @Setup(Level.Trial)
  def initLib(): Unit =
    zipFs = FileSystems.newFileSystem(zip.toNioPath)
    file = Path.fromFsPath(
      zipFs,
      fileName,
    )
    lib = ZipLib.make[IO](file)
  end initLib
  
  @TearDown(Level.Trial)
  def closeFs(): Unit = zipFs.close()
  
  @Benchmark
  def streamFoldStrings: String =
    lib.streamFoldStrings.unsafeRunSync()

  @Benchmark
  def streamFoldChunks: String =
    lib.streamFoldChunks.unsafeRunSync()

  @Benchmark
  def nioReadAllBytes: String =
    lib.nioReadAllBytes.unsafeRunSync()

  @Benchmark
  def nioInputStream: String =
    lib.nioInputStream.unsafeRunSync()

  @Benchmark
  def fs2ReadAllBytes: String =
    lib.fs2ReadAllBytes.unsafeRunSync()

end ZipBench
