package edu.utd.aos.gfs.references;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Constants and references used across project.
 * 
 * @author pankaj
 *
 */
public class GFSReferences {

	/**
	 * System Property that provides URI for the config.
	 */
	public static final String KEY_GFS_CONFIG = "gfs.config";

	/**
	 * Exit code for errors and exception.
	 */
	public static final int CONST_CODE_ERROR = 1;

	/**
	 * Heart do beat!!
	 */
	public static final String HEARTBEAT = "HEARTBEAT";

	/**
	 * Command used by Client to create a new file.
	 */
	public static final String CREATE = "CREATE";

	/**
	 * Used by chunk server to send an acknowledgement after creating a new chunk.
	 */
	public static final String CREATE_ACK = "CREATE_ACK";

	/**
	 * Command used by client to read a file.
	 */
	public static final String READ = "READ";

	/**
	 * Used by chunk while sending content for read command.
	 */
	public static final String CONTENT = "CONTENT";

	/**
	 * Command used by client to append to a file.
	 */
	public static final String APPEND = "APPEND";

	/**
	 * Separator used while splitting. Backslash for Regex Escape.
	 */
	public static final String REC_SEPARATOR = "\\|\\|";

	/**
	 * Separator used while sending.
	 */
	public static final String SEND_SEPARATOR = "||";

	/**
	 * File for Holding File-to-Chunk count.
	 */
	public static final String CHUNK_FILE_COUNT = "chunkCount";

	/**
	 * Extension of the file.
	 */
	public static final String FILE_EXT = ".txt";

	/**
	 * Separator used in storing file-to-chunk count.
	 */
	public static final String MFILE_SEPARATOR = "=";

	/**
	 * Separator used in storing file-to-chunk count.
	 */
	public static final String MCHUNK_SEPARATOR = ",";
	/**
	 * Separator used while sending.
	 */
	public static final String NEW_LINE = "\n";
	/**
	 * Chunk Name Prefix. An integer to be appended to denote unique chunk number.
	 */
	public static final String CHUNK_PREFIX = "chunk";
	/**
	 * Chunk Name Prefix
	 */
	public static final String TOTAL_CHUNKS = "TotalChunks";

	/**
	 * Chunk size.
	 */
	public static final int CHUNK_SIZE = 4096;

	/**
	 * Success resposne for client.
	 */
	public static final String CREATE_SUC = "CREATE_SUCCESS";
	/*
	 * READ response chunk to client.
	 */
	public static final String READ_CONTENT = "CONTENT";

	/**
	 * Constant encoding to be used.
	 */

	public static final Charset ENCODING = StandardCharsets.UTF_8;

	/**
	 * Pad wit Null to chunks
	 */
	public static final String PAD_NULL = "PAD_NULL";

	/**
	 * Ack for pad null.
	 */
	public static final String PAD_NULL_ACK = "PAD_NULL_ACK";

	/**
	 * Path separator for linux file system.
	 */
	public static final String PATH_SEPARATOR = "/";

	/**
	 * Send by meta to chunk server to create a new chunk.
	 */
	public static final String CREATE_CHUNK = "CREATE_CHUNK";

	/**
	 * Ack for successful chunk creation.
	 */
	public static final String CREATE_CHUNK_ACK = "CREATE_CHUNK_ACK";

	/**
	 * READY_TO_APPEND when buffered content.
	 */
	public static final String READY_TO_APPEND = "READY_TO_APPEND";

	/**
	 * COMMIT message for append.
	 */
	public static final String COMMIT = "COMMIT";

	/**
	 * ACK used after commit.
	 */
	public static final String COMMIT_ACK = "COMMIT_ACK";

	/**
	 * APPEND send from client to meta
	 */
	public static final String APPEND_ACK_META = "APPEND_ACK_META";

	/**
	 * RECOVER post failure.
	 */
	public static final String RECOVER = "RECOVER";

	/**
	 * Chunk server to respond with latest data.
	 */
	public static final String SEND_LATEST_DATA = "SEND_LATEST_DATA";

	/**
	 * To receive latest data.
	 */
	public static final String RECEIVE_LATEST_DATA = "RECEIVE_LATEST_DATA";

	/**
	 * Mark chunk as dead.
	 */
	public static final String DEAD = "DEAD";

	/**
	 * Mark chunk as being recovered.
	 */
	public static final String IN_RECOVERY = "IN_RECOVERY";

	/**
	 * Timeout for Client-Chunk READ
	 */
	public static final int TIMEOUT = 6;

	/**
	 * Display meta's current heart beat message.
	 */
	public static final String TENDERFEELINGS = "TENDERFEELINGS";

	/**
	 * Private constructor for utility class.
	 */

	private GFSReferences() {

	}
}
