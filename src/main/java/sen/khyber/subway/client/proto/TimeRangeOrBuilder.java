// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: gtfs-realtime.proto

package sen.khyber.subway.client.proto;

public interface TimeRangeOrBuilder extends
    // @@protoc_insertion_point(interface_extends:transit_realtime.TimeRange)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Start time, in POSIX time (i.e., number of seconds since January 1st 1970
   * 00:00:00 UTC).
   * If missing, the interval starts at minus infinity.
   * </pre>
   *
   * <code>optional uint64 start = 1;</code>
   */
  boolean hasStart();
  /**
   * <pre>
   * Start time, in POSIX time (i.e., number of seconds since January 1st 1970
   * 00:00:00 UTC).
   * If missing, the interval starts at minus infinity.
   * </pre>
   *
   * <code>optional uint64 start = 1;</code>
   */
  long getStart();

  /**
   * <pre>
   * End time, in POSIX time (i.e., number of seconds since January 1st 1970
   * 00:00:00 UTC).
   * If missing, the interval ends at plus infinity.
   * </pre>
   *
   * <code>optional uint64 end = 2;</code>
   */
  boolean hasEnd();
  /**
   * <pre>
   * End time, in POSIX time (i.e., number of seconds since January 1st 1970
   * 00:00:00 UTC).
   * If missing, the interval ends at plus infinity.
   * </pre>
   *
   * <code>optional uint64 end = 2;</code>
   */
  long getEnd();
}
