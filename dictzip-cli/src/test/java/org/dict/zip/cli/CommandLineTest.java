package org.dict.zip.cli;

import org.dict.zip.DictZipHeader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Created by miurahr on 16/04/09.
 */
public class CommandLineTest {
    /**
     * Test Parser help.
     */
    @Test
    public void testParseHelp() {
        final String[] argv = new String[1];
        argv[0] = "-h";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    /**
     * Test parser helpLong.
     */
    @Test
    public void testParseHelpLong() {
        final String[] argv = new String[1];
        argv[0] = "--help";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    /**
     * Test parser version.
     */
    @Test
    public void testParseVersion() {
        final String[] argv = new String[1];
        argv[0] = "-v";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    /**
     * Test parser VersionLong.
     */
    @Test
    public void testParseVersionLong() {
        final String[] argv = new String[1];
        argv[0] = "--version";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    /**
     * Test parser License.
     */
    @Test
    public void testParseLicense() {
        final String[] argv = new String[1];
        argv[0] = "-L";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    /**
     * Test parser License long.
     */
    @Test
    public void testParseLicenseLong() {
        final String[] argv = new String[1];
        argv[0] = "--license";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 1);
    }

    /**
     * Test parser Keep.
     */
    @Test
    public void testParseKeep() {
        final String[] argv = new String[1];
        argv[0] = "-k";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isKeep());
    }

    /**
     * Test parser KeepLong.
     */
    @Test
    public void testParseKeepLong() {
        final String[] argv = new String[1];
        argv[0] = "--keep";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isKeep());
    }

    /**
     * Test parser stdout.
     */
    @Test
    public void testParseStdout() {
        final String[] argv = new String[1];
        argv[0] = "-c";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isStdout());
    }

    /**
     * Test parser stdout long.
     */
    @Test
    public void testParseStdoutLong() {
        final String[] argv = new String[1];
        argv[0] = "--stdout";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isStdout());
    }

    /**
     * Test parser force.
     */
    @Test
    public void testParseForce() {
        final String[] argv = new String[1];
        argv[0] = "-f";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isForce());
    }

    /**
     * Test parser force long.
     */
    @Test
    public void testParseForceLong() {
        final String[] argv = new String[1];
        argv[0] = "--force";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isForce());
    }

    /**
     * Test parser decompresss.
     */
    @Test
    public void testParseDecompress() {
        final String[] argv = new String[1];
        argv[0] = "-d";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isDecompress());
    }

    /**
     * Test parser decompress long.
     */
    @Test
    public void testParseDecompressLong() {
        final String[] argv = new String[1];
        argv[0] = "--decompress";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isDecompress());
    }

    /**
     * Test parser List.
     */
    @Test
    public void testParseList() {
        final String[] argv = new String[1];
        argv[0] = "-l";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isList());
    }

    /**
     * Test parser list long version.
     */
    @Test
    public void testParseListLong() {
        final String[] argv = new String[1];
        argv[0] = "--list";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.options.isList());
    }

    /**
     * test parsre leve fast.
     */
    @Test
    public void testParseLevelFast() {
        final String[] argv = new String[1];
        argv[0] = "-1";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getLevel(), DictZipHeader.CompressionLevel.BEST_SPEED);
    }

    /**
     * Test parser level fast long version.
     */
    @Test
    public void testParseLevelFastLong() {
        final String[] argv = new String[1];
        argv[0] = "--fast";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getLevel(), DictZipHeader.CompressionLevel.BEST_SPEED);
    }

    /**
     * Test parser Level best.
     */
    @Test
    public void testParseLevelBest() {
        final String[] argv = new String[1];
        argv[0] = "-9";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getLevel(), DictZipHeader.CompressionLevel.BEST_COMPRESSION);
    }

    /**
     * Test parser Level best long version.
     */
    @Test
    public void testParseLevelBestLong() {
        final String[] argv = new String[1];
        argv[0] = "--best";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getLevel(), DictZipHeader.CompressionLevel.BEST_COMPRESSION);
    }

    /**
     * test parser level default.
     */
    @Test
    public void testParseLevelDefault() {
        final String[] argv = new String[1];
        argv[0] = "-6";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getLevel(), DictZipHeader.CompressionLevel.DEFAULT_COMPRESSION);
    }

    /**
     * test parser target.
     */
    @Test
    public void testParseTarget() {
        final String[] argv = new String[1];
        argv[0] = "target_filename";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertTrue(commandLine.getTargetFiles().equals(new ArrayList<String>() {
            {
                add("target_filename");
            }
        }
        ));
    }

    /**
     * Test parser number hex.
     * @throws NumberFormatException when giving non-number argument.
     */
    @Test
    public void testParseNumberHex() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 0x123A";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getStart(), 0x123A);
    }

    /**
     * Test parser Number octet.
     * @throws NumberFormatException when giving non-number argument.
     */
    @Test
    public void testParseNumberOct() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 01237";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getStart(), 671);
    }

    /**
     * Test parser number decimal.
     * @throws NumberFormatException when giving non-number argument.
     */
    @Test
    public void testParseNumberDec() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 1239";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getStart(), 1239);
    }

    /**
     * Test parser size.
     * @throws NumberFormatException when giving non-number argument.
     */
    @Test
    public void testParseSize() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-e 1239";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 0);
        assertEquals(commandLine.options.getSize(), 1239);
    }

    /**
     * Test parser start bad number.
     * @throws NumberFormatException when giving bad number.
     */
    @Test
    public void testParseStartBadnum() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 123A";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 2);
    }

    /**
     * Test parser start badnum octet.
     * @throws NumberFormatException when giving bad number.
     */
    @Test
    public void testParseStartBadnumOct() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 0Q23A";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 2);
    }

    /**
     * Test parser start badnum hex.
     * @throws NumberFormatException when giving bad number.
     */
    @Test
    public void testParseStartBadnumHex() throws NumberFormatException {
        final String[] argv = new String[1];
        argv[0] = "-s 0xQ23A";
        CommandLine commandLine = new CommandLine();
        assertEquals(commandLine.parse(argv), 2);
    }
}
