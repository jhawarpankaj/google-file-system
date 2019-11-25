package edu.utd.aos.gfs.references;

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
	 * Command used by client to read a file.
	 */
	public static final String READ = "READ";

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
	 * Chunk Name Prefix
	 */
	public static final String CHUNK_PREFIX = "chunk";
	/**
	 * Chunk Name Prefix
	 */
	public static final String TOTAL_CHUNKS = "TotalChunks";

	/**
	 * Chunk Size
	 */
	public static final int CHUNK_SIZE = 4096;

	/**
	 * Private constructor for utility class.
	 */
	private GFSReferences() {

	}
}
