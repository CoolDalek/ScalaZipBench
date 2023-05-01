package com.scalamandra

import fs2.*
import fs2.io.file.*
import cats.effect.*
import cats.*
import cats.syntax.all.*

import java.nio.charset.Charset
import java.nio.file.{FileSystem, FileSystems, Files as JFiles}


trait ZipLib[F[_]]:

  def streamFoldStrings: F[String]
  def streamFoldChunks: F[String]
  def nioReadAllBytes: F[String]
  def nioInputStream: F[String]
  def fs2ReadAllBytes: F[String]

object ZipLib:

  private val readFlags = Flags(Flag.Read)

  def make[F[_]: Files: Sync](file: Path)(using Compiler[F, F]): ZipLib[F] = new:

    override def streamFoldStrings: F[String] =
      Files[F].readUtf8(file)
        .compile
        .string

    override def streamFoldChunks: F[String] =
      Files[F].readAll(file)
        .chunks
        .compile
        .foldMonoid
        .map(c => mkString(c.toArray))

    private inline def mkString(inline array: => Array[Byte]): String =
      new String(
        array,
        Charset.forName("UTF-8")
      )

    override def nioReadAllBytes: F[String] =
      Sync[F].blocking {
        mkString {
          JFiles.readAllBytes(file.toNioPath)
        }
      }

    override def nioInputStream: F[String] =
      Sync[F].blocking {
        mkString {
          val in = JFiles.newInputStream(file.toNioPath)
          try in.readAllBytes()
          finally in.close()
        }
      }

    override def fs2ReadAllBytes: F[String] =
      Files[F].open(file, readFlags).use { handle =>
        for
          size <- handle.size
          _ <- ApplicativeThrow[F].raiseWhen(size > Int.MaxValue.toLong) {
            IllegalArgumentException("File is too big.")
          }
          bytes <- handle.read(size.toInt, 0)
          array = bytes
            .map(_.toArray)
            .getOrElse(Array.empty[Byte])
        yield mkString(array)
      }

  end make

end ZipLib
