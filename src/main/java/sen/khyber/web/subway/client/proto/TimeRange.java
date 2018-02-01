// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: gtfs-realtime.proto

package sen.khyber.web.subway.client.proto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.OneofDescriptor;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;

/**
 * <pre>
 * A time interval. The interval is considered active at time 't' if 't' is
 * greater than or equal to the start time and less than the end time.
 * </pre>
 * <p>
 * Protobuf type {@code transit_realtime.TimeRange}
 */
public final class TimeRange extends GeneratedMessageV3 implements TimeRangeOrBuilder {
    
    private static final long serialVersionUID = 0L;
    
    // Use TimeRange.newBuilder() to construct.
    private TimeRange(final GeneratedMessageV3.Builder<?> builder) {
        super(builder);
    }
    
    private TimeRange() {
        start_ = 0L;
        end_ = 0L;
    }
    
    @Override
    public final UnknownFieldSet getUnknownFields() {
        return unknownFields;
    }
    
    private TimeRange(final CodedInputStream input, final ExtensionRegistryLite extensionRegistry)
            throws InvalidProtocolBufferException {
        this();
        final int mutable_bitField0_ = 0;
        final UnknownFieldSet.Builder unknownFields = UnknownFieldSet.newBuilder();
        try {
            boolean done = false;
            while (!done) {
                final int tag = input.readTag();
                switch (tag) {
                    case 0:
                        done = true;
                        break;
                    default: {
                        if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                            done = true;
                        }
                        break;
                    }
                    case 8: {
                        bitField0_ |= 0x00000001;
                        start_ = input.readUInt64();
                        break;
                    }
                    case 16: {
                        bitField0_ |= 0x00000002;
                        end_ = input.readUInt64();
                        break;
                    }
                }
            }
        } catch (final InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(this);
        } catch (final IOException e) {
            throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
        } finally {
            this.unknownFields = unknownFields.build();
            makeExtensionsImmutable();
        }
    }
    
    public static final Descriptor getDescriptor() {
        return GtfsRealtime.internal_static_transit_realtime_TimeRange_descriptor;
    }
    
    @Override
    protected FieldAccessorTable internalGetFieldAccessorTable() {
        return GtfsRealtime.internal_static_transit_realtime_TimeRange_fieldAccessorTable
                .ensureFieldAccessorsInitialized(TimeRange.class, Builder.class);
    }
    
    private int bitField0_;
    public static final int START_FIELD_NUMBER = 1;
    private long start_;
    
    /**
     * <pre>
     * Start time, in POSIX time (i.e., number of seconds since January 1st 1970
     * 00:00:00 UTC).
     * If missing, the interval starts at minus infinity.
     * </pre>
     * <p>
     * <code>optional uint64 start = 1;</code>
     */
    @Override
    public boolean hasStart() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    
    /**
     * <pre>
     * Start time, in POSIX time (i.e., number of seconds since January 1st 1970
     * 00:00:00 UTC).
     * If missing, the interval starts at minus infinity.
     * </pre>
     * <p>
     * <code>optional uint64 start = 1;</code>
     */
    @Override
    public long getStart() {
        return start_;
    }
    
    public static final int END_FIELD_NUMBER = 2;
    private long end_;
    
    /**
     * <pre>
     * End time, in POSIX time (i.e., number of seconds since January 1st 1970
     * 00:00:00 UTC).
     * If missing, the interval ends at plus infinity.
     * </pre>
     * <p>
     * <code>optional uint64 end = 2;</code>
     */
    @Override
    public boolean hasEnd() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    
    /**
     * <pre>
     * End time, in POSIX time (i.e., number of seconds since January 1st 1970
     * 00:00:00 UTC).
     * If missing, the interval ends at plus infinity.
     * </pre>
     * <p>
     * <code>optional uint64 end = 2;</code>
     */
    @Override
    public long getEnd() {
        return end_;
    }
    
    private byte memoizedIsInitialized = -1;
    
    @Override
    public final boolean isInitialized() {
        final byte isInitialized = memoizedIsInitialized;
        if (isInitialized == 1) {return true;}
        if (isInitialized == 0) {return false;}
        
        memoizedIsInitialized = 1;
        return true;
    }
    
    @Override
    public void writeTo(final CodedOutputStream output) throws IOException {
        if (((bitField0_ & 0x00000001) == 0x00000001)) {
            output.writeUInt64(1, start_);
        }
        if (((bitField0_ & 0x00000002) == 0x00000002)) {
            output.writeUInt64(2, end_);
        }
        unknownFields.writeTo(output);
    }
    
    @Override
    public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1) {return size;}
        
        size = 0;
        if (((bitField0_ & 0x00000001) == 0x00000001)) {
            size += CodedOutputStream.computeUInt64Size(1, start_);
        }
        if (((bitField0_ & 0x00000002) == 0x00000002)) {
            size += CodedOutputStream.computeUInt64Size(2, end_);
        }
        size += unknownFields.getSerializedSize();
        memoizedSize = size;
        return size;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimeRange)) {
            return super.equals(obj);
        }
        final TimeRange other = (TimeRange) obj;
        
        boolean result = true;
        result = result && (hasStart() == other.hasStart());
        if (hasStart()) {
            result = result && (getStart() == other.getStart());
        }
        result = result && (hasEnd() == other.hasEnd());
        if (hasEnd()) {
            result = result && (getEnd() == other.getEnd());
        }
        result = result && unknownFields.equals(other.unknownFields);
        return result;
    }
    
    @Override
    public int hashCode() {
        if (memoizedHashCode != 0) {
            return memoizedHashCode;
        }
        int hash = 41;
        hash = (19 * hash) + getDescriptor().hashCode();
        if (hasStart()) {
            hash = (37 * hash) + START_FIELD_NUMBER;
            hash = (53 * hash) + Internal.hashLong(getStart());
        }
        if (hasEnd()) {
            hash = (37 * hash) + END_FIELD_NUMBER;
            hash = (53 * hash) + Internal.hashLong(getEnd());
        }
        hash = (29 * hash) + unknownFields.hashCode();
        memoizedHashCode = hash;
        return hash;
    }
    
    public static TimeRange parseFrom(final ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }
    
    public static TimeRange parseFrom(final ByteBuffer data,
            final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static TimeRange parseFrom(final ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }
    
    public static TimeRange parseFrom(final ByteString data,
            final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static TimeRange parseFrom(final byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }
    
    public static TimeRange parseFrom(final byte[] data,
            final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static TimeRange parseFrom(final InputStream input) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static TimeRange parseFrom(final InputStream input,
            final ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static TimeRange parseDelimitedFrom(final InputStream input) throws IOException {
        return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static TimeRange parseDelimitedFrom(final InputStream input,
            final ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static TimeRange parseFrom(final CodedInputStream input) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static TimeRange parseFrom(final CodedInputStream input,
            final ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    
    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(final TimeRange prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    
    @Override
    public Builder toBuilder() {
        return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
    }
    
    @Override
    protected Builder newBuilderForType(final BuilderParent parent) {
        final Builder builder = new Builder(parent);
        return builder;
    }
    
    /**
     * <pre>
     * A time interval. The interval is considered active at time 't' if 't' is
     * greater than or equal to the start time and less than the end time.
     * </pre>
     * <p>
     * Protobuf type {@code transit_realtime.TimeRange}
     */
    public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements
            // @@protoc_insertion_point(builder_implements:transit_realtime.TimeRange)
            TimeRangeOrBuilder {
        
        public static final Descriptor getDescriptor() {
            return GtfsRealtime.internal_static_transit_realtime_TimeRange_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return GtfsRealtime.internal_static_transit_realtime_TimeRange_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(TimeRange.class, Builder.class);
        }
        
        // Construct using sen.khyber.subway.client.proto.TimeRange.newBuilder()
        private Builder() {
            maybeForceBuilderInitialization();
        }
        
        private Builder(final BuilderParent parent) {
            super(parent);
            maybeForceBuilderInitialization();
        }
        
        private void maybeForceBuilderInitialization() {
            if (GeneratedMessageV3.alwaysUseFieldBuilders) {
            }
        }
        
        @Override
        public Builder clear() {
            super.clear();
            start_ = 0L;
            bitField0_ = (bitField0_ & ~0x00000001);
            end_ = 0L;
            bitField0_ = (bitField0_ & ~0x00000002);
            return this;
        }
        
        @Override
        public Descriptor getDescriptorForType() {
            return GtfsRealtime.internal_static_transit_realtime_TimeRange_descriptor;
        }
        
        @Override
        public TimeRange getDefaultInstanceForType() {
            return getDefaultInstance();
        }
        
        @Override
        public TimeRange build() {
            final TimeRange result = buildPartial();
            if (!result.isInitialized()) {
                throw newUninitializedMessageException(result);
            }
            return result;
        }
        
        @Override
        public TimeRange buildPartial() {
            final TimeRange result = new TimeRange(this);
            final int from_bitField0_ = bitField0_;
            int to_bitField0_ = 0;
            if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
                to_bitField0_ |= 0x00000001;
            }
            result.start_ = start_;
            if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
                to_bitField0_ |= 0x00000002;
            }
            result.end_ = end_;
            result.bitField0_ = to_bitField0_;
            onBuilt();
            return result;
        }
        
        @Override
        public Builder clone() {
            return super.clone();
        }
        
        @Override
        public Builder setField(final FieldDescriptor field, final Object value) {
            return super.setField(field, value);
        }
        
        @Override
        public Builder clearField(final FieldDescriptor field) {
            return super.clearField(field);
        }
        
        @Override
        public Builder clearOneof(final OneofDescriptor oneof) {
            return super.clearOneof(oneof);
        }
        
        @Override
        public Builder setRepeatedField(final FieldDescriptor field, final int index,
                final Object value) {
            return super.setRepeatedField(field, index, value);
        }
        
        @Override
        public Builder addRepeatedField(final FieldDescriptor field, final Object value) {
            return super.addRepeatedField(field, value);
        }
        
        @Override
        public Builder mergeFrom(final Message other) {
            if (other instanceof TimeRange) {
                return mergeFrom((TimeRange) other);
            } else {
                super.mergeFrom(other);
                return this;
            }
        }
        
        public Builder mergeFrom(final TimeRange other) {
            if (other == getDefaultInstance()) {return this;}
            if (other.hasStart()) {
                setStart(other.getStart());
            }
            if (other.hasEnd()) {
                setEnd(other.getEnd());
            }
            mergeUnknownFields(other.unknownFields);
            onChanged();
            return this;
        }
        
        @Override
        public final boolean isInitialized() {
            return true;
        }
        
        @Override
        public Builder mergeFrom(final CodedInputStream input,
                final ExtensionRegistryLite extensionRegistry) throws IOException {
            TimeRange parsedMessage = null;
            try {
                parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (final InvalidProtocolBufferException e) {
                parsedMessage = (TimeRange) e.getUnfinishedMessage();
                throw e.unwrapIOException();
            } finally {
                if (parsedMessage != null) {
                    mergeFrom(parsedMessage);
                }
            }
            return this;
        }
        
        private int bitField0_;
        
        private long start_;
        
        /**
         * <pre>
         * Start time, in POSIX time (i.e., number of seconds since January 1st 1970
         * 00:00:00 UTC).
         * If missing, the interval starts at minus infinity.
         * </pre>
         * <p>
         * <code>optional uint64 start = 1;</code>
         */
        @Override
        public boolean hasStart() {
            return ((bitField0_ & 0x00000001) == 0x00000001);
        }
        
        /**
         * <pre>
         * Start time, in POSIX time (i.e., number of seconds since January 1st 1970
         * 00:00:00 UTC).
         * If missing, the interval starts at minus infinity.
         * </pre>
         * <p>
         * <code>optional uint64 start = 1;</code>
         */
        @Override
        public long getStart() {
            return start_;
        }
        
        /**
         * <pre>
         * Start time, in POSIX time (i.e., number of seconds since January 1st 1970
         * 00:00:00 UTC).
         * If missing, the interval starts at minus infinity.
         * </pre>
         * <p>
         * <code>optional uint64 start = 1;</code>
         */
        public Builder setStart(final long value) {
            bitField0_ |= 0x00000001;
            start_ = value;
            onChanged();
            return this;
        }
        
        /**
         * <pre>
         * Start time, in POSIX time (i.e., number of seconds since January 1st 1970
         * 00:00:00 UTC).
         * If missing, the interval starts at minus infinity.
         * </pre>
         * <p>
         * <code>optional uint64 start = 1;</code>
         */
        public Builder clearStart() {
            bitField0_ = (bitField0_ & ~0x00000001);
            start_ = 0L;
            onChanged();
            return this;
        }
        
        private long end_;
        
        /**
         * <pre>
         * End time, in POSIX time (i.e., number of seconds since January 1st 1970
         * 00:00:00 UTC).
         * If missing, the interval ends at plus infinity.
         * </pre>
         * <p>
         * <code>optional uint64 end = 2;</code>
         */
        @Override
        public boolean hasEnd() {
            return ((bitField0_ & 0x00000002) == 0x00000002);
        }
        
        /**
         * <pre>
         * End time, in POSIX time (i.e., number of seconds since January 1st 1970
         * 00:00:00 UTC).
         * If missing, the interval ends at plus infinity.
         * </pre>
         * <p>
         * <code>optional uint64 end = 2;</code>
         */
        @Override
        public long getEnd() {
            return end_;
        }
        
        /**
         * <pre>
         * End time, in POSIX time (i.e., number of seconds since January 1st 1970
         * 00:00:00 UTC).
         * If missing, the interval ends at plus infinity.
         * </pre>
         * <p>
         * <code>optional uint64 end = 2;</code>
         */
        public Builder setEnd(final long value) {
            bitField0_ |= 0x00000002;
            end_ = value;
            onChanged();
            return this;
        }
        
        /**
         * <pre>
         * End time, in POSIX time (i.e., number of seconds since January 1st 1970
         * 00:00:00 UTC).
         * If missing, the interval ends at plus infinity.
         * </pre>
         * <p>
         * <code>optional uint64 end = 2;</code>
         */
        public Builder clearEnd() {
            bitField0_ = (bitField0_ & ~0x00000002);
            end_ = 0L;
            onChanged();
            return this;
        }
        
        @Override
        public final Builder setUnknownFields(final UnknownFieldSet unknownFields) {
            return super.setUnknownFields(unknownFields);
        }
        
        @Override
        public final Builder mergeUnknownFields(final UnknownFieldSet unknownFields) {
            return super.mergeUnknownFields(unknownFields);
        }
        
        
        // @@protoc_insertion_point(builder_scope:transit_realtime.TimeRange)
    }
    
    // @@protoc_insertion_point(class_scope:transit_realtime.TimeRange)
    private static final TimeRange DEFAULT_INSTANCE;
    
    static {
        DEFAULT_INSTANCE = new TimeRange();
    }
    
    public static TimeRange getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }
    
    @Deprecated public static final Parser<TimeRange> PARSER = new AbstractParser<>() {
        
        @Override
        public TimeRange parsePartialFrom(final CodedInputStream input,
                final ExtensionRegistryLite extensionRegistry)
                throws InvalidProtocolBufferException {
            return new TimeRange(input, extensionRegistry);
        }
    };
    
    public static Parser<TimeRange> parser() {
        return PARSER;
    }
    
    @Override
    public Parser<TimeRange> getParserForType() {
        return PARSER;
    }
    
    @Override
    public TimeRange getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }
    
}