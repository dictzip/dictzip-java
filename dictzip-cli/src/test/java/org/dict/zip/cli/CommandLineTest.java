package org.dict.zip.cli;

import org.dict.zip.DictZipHeader;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.*;

/**
 * Created by miurahr on 16/04/09.
 */
public class CommandLineTest {
    @Test
    public void testParse_help() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "-h";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    @Test
    public void testParse_helpLong() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "--help";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    @Test
    public void testParse_version() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "-v";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    @Test
    public void testParse_versionLong() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "--version";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    @Test
    public void testParse_license() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "-L";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    @Test
    public void testParse_licenseLong() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "--license";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    @Test
    public void testParse_keep() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "-k";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isKeep());
    }

    @Test
    public void testParse_keepLong() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "--keep";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isKeep());
    }

    @Test
    public void testParse_stdout() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "-c";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isStdout());
    }

    @Test
    public void testParse_stdoutLong() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "--stdout";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isStdout());
    }

    @Test
    public void testParse_force() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "-f";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isForce());
    }

    @Test
    public void testParse_forceLong() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "--force";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isForce());
    }

    @Test
    public void testParse_decompress() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "-d";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isDecompress());
    }

    @Test
    public void testParse_decompressLong() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "--decompress";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isDecompress());
    }

    @Test
    public void testParse_list() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "-l";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isList());
    }

    @Test
    public void testParse_listLong() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "--list";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isList());
    }

    @Test
    public void testParse_levelFast() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "-1";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getLevel(), DictZipHeader.CompressionLevel.BEST_SPEED);
    }

    @Test
    public void testParse_levelFastLong() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "--fast";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getLevel(), DictZipHeader.CompressionLevel.BEST_SPEED);
    }

    @Test
    public void testParse_levelBest() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "-9";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getLevel(), DictZipHeader.CompressionLevel.BEST_COMPRESSION);
    }

    @Test
    public void testParse_levelBestLong() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "--best";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getLevel(), DictZipHeader.CompressionLevel.BEST_COMPRESSION);
    }

    @Test
    public void testParse_levelDefault() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "-6";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getLevel(), DictZipHeader.CompressionLevel.DEFAULT_COMPRESSION);
    }

    @Test
    public void testParse_target() throws Exception {
        final String[] argv = new String[1];
        argv[0] = "target_filename";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.getTargetFiles().equals(new ArrayList<String> (){{add("target_filename");}}));
    }

    @Test
    public void testParseNumber_hex() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 0x123A";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getStart(), 0x123A);
    }

    @Test
    public void testParseNumber_oct() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 01237";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getStart(), 671);
    }

    @Test
    public void testParseNumber_dec() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 1239";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getStart(), 1239);
    }

    @Test
    public void testParse_size() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-e 1239";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getSize(), 1239);
    }


    @Test
    public void testParse_start_badnum() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 123A";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 2);
    }

    @Test
    public void testParse_start_badnumOct() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 0Q23A";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 2);
    }

    @Test
    public void testParse_start_badnumHex() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 0xQ23A";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 2);
    }
}