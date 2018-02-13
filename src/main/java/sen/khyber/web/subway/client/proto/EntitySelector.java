// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: gtfs-realtime.proto

package sen.khyber.web.subway.client.proto;

import sen.khyber.proto.ProtoUtils;

import lombok.AccessLevel;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.OneofDescriptor;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.GeneratedMessageV3.ExtendableMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.SingleFieldBuilderV3;
import com.google.protobuf.UnknownFieldSet;

/**
 * <pre>
 * A selector for an entity in a GTFS feed.
 * </pre>
 * <p>
 * Protobuf type {@code transit_realtime.EntitySelector}
 */
public final class EntitySelector extends ExtendableMessage<EntitySelector>
        implements EntitySelectorOrBuilder {
    
    private static final long serialVersionUID = 0L;
    
    // Use EntitySelector.newBuilder() to construct.
    private EntitySelector(final ExtendableBuilder<EntitySelector, ?> builder) {
        super(builder);
    }
    
    private EntitySelector() {
        agencyId_ = "";
        routeId_ = "";
        routeType_ = 0;
        stopId_ = "";
    }
    
    @Override
    public final UnknownFieldSet getUnknownFields() {
        return unknownFields;
    }
    
    private EntitySelector(final CodedInputStream input,
            final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
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
                    case 10: {
                        final ByteString bs = input.readBytes();
                        bitField0_ |= 0x00000001;
                        agencyId_ = bs;
                        break;
                    }
                    case 18: {
                        final ByteString bs = input.readBytes();
                        bitField0_ |= 0x00000002;
                        routeId_ = bs;
                        break;
                    }
                    case 24: {
                        bitField0_ |= 0x00000004;
                        routeType_ = input.readInt32();
                        break;
                    }
                    case 34: {
                        TripDescriptor.Builder subBuilder = null;
                        if (((bitField0_ & 0x00000008) == 0x00000008)) {
                            subBuilder = trip_.toBuilder();
                        }
                        trip_ = input.readMessage(TripDescriptor.PARSER, extensionRegistry);
                        if (subBuilder != null) {
                            subBuilder.mergeFrom(trip_);
                            trip_ = subBuilder.buildPartial();
                        }
                        bitField0_ |= 0x00000008;
                        break;
                    }
                    case 42: {
                        final ByteString bs = input.readBytes();
                        bitField0_ |= 0x00000010;
                        stopId_ = bs;
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
        return GtfsRealtime.internal_static_transit_realtime_EntitySelector_descriptor;
    }
    
    @Override
    protected FieldAccessorTable internalGetFieldAccessorTable() {
        return GtfsRealtime.internal_static_transit_realtime_EntitySelector_fieldAccessorTable
                .ensureFieldAccessorsInitialized(EntitySelector.class, Builder.class);
    }
    
    private int bitField0_;
    public static final int AGENCY_ID_FIELD_NUMBER = 1;
    private volatile @Setter(AccessLevel.PRIVATE) Object agencyId_;
    
    /**
     * <pre>
     * The values of the fields should correspond to the appropriate fields in the
     * GTFS feed.
     * At least one specifier must be given. If several are given, then the
     * matching has to apply to all the given specifiers.
     * </pre>
     * <p>
     * <code>optional string agency_id = 1;</code>
     */
    @Override
    public boolean hasAgencyId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    
    /**
     * <pre>
     * The values of the fields should correspond to the appropriate fields in the
     * GTFS feed.
     * At least one specifier must be given. If several are given, then the
     * matching has to apply to all the given specifiers.
     * </pre>
     * <p>
     * <code>optional string agency_id = 1;</code>
     */
    @Override
    public String getAgencyId() {
        return ProtoUtils.asString(agencyId_, this::setAgencyId_);
    }
    
    /**
     * <pre>
     * The values of the fields should correspond to the appropriate fields in the
     * GTFS feed.
     * At least one specifier must be given. If several are given, then the
     * matching has to apply to all the given specifiers.
     * </pre>
     * <p>
     * <code>optional string agency_id = 1;</code>
     */
    @Override
    public ByteString getAgencyIdBytes() {
        return ProtoUtils.asBytes(agencyId_, this::setAgencyId_);
    }
    
    public static final int ROUTE_ID_FIELD_NUMBER = 2;
    @Setter(AccessLevel.PRIVATE) private volatile Object routeId_;
    
    /**
     * <code>optional string route_id = 2;</code>
     */
    @Override
    public boolean hasRouteId() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    
    /**
     * <code>optional string route_id = 2;</code>
     */
    @Override
    public String getRouteId() {
        return ProtoUtils.asString(routeId_, this::setRouteId_);
    }
    
    /**
     * <code>optional string route_id = 2;</code>
     */
    @Override
    public ByteString getRouteIdBytes() {
        return ProtoUtils.asBytes(routeId_, this::setRouteId_);
    }
    
    public static final int ROUTE_TYPE_FIELD_NUMBER = 3;
    private int routeType_;
    
    /**
     * <pre>
     * corresponds to route_type in GTFS.
     * </pre>
     * <p>
     * <code>optional int32 route_type = 3;</code>
     */
    @Override
    public boolean hasRouteType() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    
    /**
     * <pre>
     * corresponds to route_type in GTFS.
     * </pre>
     * <p>
     * <code>optional int32 route_type = 3;</code>
     */
    @Override
    public int getRouteType() {
        return routeType_;
    }
    
    public static final int TRIP_FIELD_NUMBER = 4;
    private TripDescriptor trip_;
    
    /**
     * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
     */
    @Override
    public boolean hasTrip() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    
    /**
     * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
     */
    @Override
    public TripDescriptor getTrip() {
        return trip_ == null ? TripDescriptor.getDefaultInstance() : trip_;
    }
    
    /**
     * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
     */
    @Override
    public TripDescriptorOrBuilder getTripOrBuilder() {
        return trip_ == null ? TripDescriptor.getDefaultInstance() : trip_;
    }
    
    public static final int STOP_ID_FIELD_NUMBER = 5;
    @Setter(AccessLevel.PRIVATE) private volatile Object stopId_;
    
    /**
     * <code>optional string stop_id = 5;</code>
     */
    @Override
    public boolean hasStopId() {
        return ((bitField0_ & 0x00000010) == 0x00000010);
    }
    
    /**
     * <code>optional string stop_id = 5;</code>
     */
    @Override
    public String getStopId() {
        return ProtoUtils.asString(stopId_, this::setStopId_);
    }
    
    /**
     * <code>optional string stop_id = 5;</code>
     */
    @Override
    public ByteString getStopIdBytes() {
        return ProtoUtils.asBytes(stopId_, this::setStopId_);
    }
    
    private byte memoizedIsInitialized = -1;
    
    @Override
    public final boolean isInitialized() {
        final byte isInitialized = memoizedIsInitialized;
        if (isInitialized == 1) {return true;}
        if (isInitialized == 0) {return false;}
        
        if (hasTrip()) {
            if (!getTrip().isInitialized()) {
                memoizedIsInitialized = 0;
                return false;
            }
        }
        if (!extensionsAreInitialized()) {
            memoizedIsInitialized = 0;
            return false;
        }
        memoizedIsInitialized = 1;
        return true;
    }
    
    @Override
    public void writeTo(final CodedOutputStream output) throws IOException {
        final ExtendableMessage<EntitySelector>.ExtensionWriter extensionWriter =
                newExtensionWriter();
        if (((bitField0_ & 0x00000001) == 0x00000001)) {
            GeneratedMessageV3.writeString(output, 1, agencyId_);
        }
        if (((bitField0_ & 0x00000002) == 0x00000002)) {
            GeneratedMessageV3.writeString(output, 2, routeId_);
        }
        if (((bitField0_ & 0x00000004) == 0x00000004)) {
            output.writeInt32(3, routeType_);
        }
        if (((bitField0_ & 0x00000008) == 0x00000008)) {
            output.writeMessage(4, getTrip());
        }
        if (((bitField0_ & 0x00000010) == 0x00000010)) {
            GeneratedMessageV3.writeString(output, 5, stopId_);
        }
        extensionWriter.writeUntil(2000, output);
        unknownFields.writeTo(output);
    }
    
    @Override
    public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1) {return size;}
        
        size = 0;
        if (((bitField0_ & 0x00000001) == 0x00000001)) {
            size += GeneratedMessageV3.computeStringSize(1, agencyId_);
        }
        if (((bitField0_ & 0x00000002) == 0x00000002)) {
            size += GeneratedMessageV3.computeStringSize(2, routeId_);
        }
        if (((bitField0_ & 0x00000004) == 0x00000004)) {
            size += CodedOutputStream.computeInt32Size(3, routeType_);
        }
        if (((bitField0_ & 0x00000008) == 0x00000008)) {
            size += CodedOutputStream.computeMessageSize(4, getTrip());
        }
        if (((bitField0_ & 0x00000010) == 0x00000010)) {
            size += GeneratedMessageV3.computeStringSize(5, stopId_);
        }
        size += extensionsSerializedSize();
        size += unknownFields.getSerializedSize();
        memoizedSize = size;
        return size;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof EntitySelector)) {
            return super.equals(obj);
        }
        final EntitySelector other = (EntitySelector) obj;
        
        boolean result = true;
        result = result && (hasAgencyId() == other.hasAgencyId());
        if (hasAgencyId()) {
            result = result && getAgencyId().equals(other.getAgencyId());
        }
        result = result && (hasRouteId() == other.hasRouteId());
        if (hasRouteId()) {
            result = result && getRouteId().equals(other.getRouteId());
        }
        result = result && (hasRouteType() == other.hasRouteType());
        if (hasRouteType()) {
            result = result && (getRouteType() == other.getRouteType());
        }
        result = result && (hasTrip() == other.hasTrip());
        if (hasTrip()) {
            result = result && getTrip().equals(other.getTrip());
        }
        result = result && (hasStopId() == other.hasStopId());
        if (hasStopId()) {
            result = result && getStopId().equals(other.getStopId());
        }
        result = result && unknownFields.equals(other.unknownFields);
        result = result && getExtensionFields().equals(other.getExtensionFields());
        return result;
    }
    
    @Override
    public int hashCode() {
        if (memoizedHashCode != 0) {
            return memoizedHashCode;
        }
        int hash = 41;
        hash = (19 * hash) + getDescriptor().hashCode();
        if (hasAgencyId()) {
            hash = (37 * hash) + AGENCY_ID_FIELD_NUMBER;
            hash = (53 * hash) + getAgencyId().hashCode();
        }
        if (hasRouteId()) {
            hash = (37 * hash) + ROUTE_ID_FIELD_NUMBER;
            hash = (53 * hash) + getRouteId().hashCode();
        }
        if (hasRouteType()) {
            hash = (37 * hash) + ROUTE_TYPE_FIELD_NUMBER;
            hash = (53 * hash) + getRouteType();
        }
        if (hasTrip()) {
            hash = (37 * hash) + TRIP_FIELD_NUMBER;
            hash = (53 * hash) + getTrip().hashCode();
        }
        if (hasStopId()) {
            hash = (37 * hash) + STOP_ID_FIELD_NUMBER;
            hash = (53 * hash) + getStopId().hashCode();
        }
        hash = hashFields(hash, getExtensionFields());
        hash = (29 * hash) + unknownFields.hashCode();
        memoizedHashCode = hash;
        return hash;
    }
    
    public static EntitySelector parseFrom(final ByteBuffer data)
            throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }
    
    public static EntitySelector parseFrom(final ByteBuffer data,
            final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static EntitySelector parseFrom(final ByteString data)
            throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }
    
    public static EntitySelector parseFrom(final ByteString data,
            final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static EntitySelector parseFrom(final byte[] data)
            throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }
    
    public static EntitySelector parseFrom(final byte[] data,
            final ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }
    
    public static EntitySelector parseFrom(final InputStream input) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static EntitySelector parseFrom(final InputStream input,
            final ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static EntitySelector parseDelimitedFrom(final InputStream input) throws IOException {
        return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input);
    }
    
    public static EntitySelector parseDelimitedFrom(final InputStream input,
            final ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    
    public static EntitySelector parseFrom(final CodedInputStream input) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input);
    }
    
    public static EntitySelector parseFrom(final CodedInputStream input,
            final ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessageV3.parseWithIOException(PARSER, input, extensionRegistry);
    }
    
    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    
    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }
    
    public static Builder newBuilder(final EntitySelector prototype) {
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
     * A selector for an entity in a GTFS feed.
     * </pre>
     * <p>
     * Protobuf type {@code transit_realtime.EntitySelector}
     */
    public static final class Builder extends ExtendableBuilder<EntitySelector, Builder> implements
            // @@protoc_insertion_point(builder_implements:transit_realtime.EntitySelector)
            EntitySelectorOrBuilder {
        
        public static final Descriptor getDescriptor() {
            return GtfsRealtime.internal_static_transit_realtime_EntitySelector_descriptor;
        }
        
        @Override
        protected FieldAccessorTable internalGetFieldAccessorTable() {
            return GtfsRealtime.internal_static_transit_realtime_EntitySelector_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(EntitySelector.class, Builder.class);
        }
        
        // Construct using sen.khyber.subway.client.proto.EntitySelector.newBuilder()
        private Builder() {
            maybeForceBuilderInitialization();
        }
        
        private Builder(final BuilderParent parent) {
            super(parent);
            maybeForceBuilderInitialization();
        }
        
        private void maybeForceBuilderInitialization() {
            if (GeneratedMessageV3.alwaysUseFieldBuilders) {
                getTripFieldBuilder();
            }
        }
        
        @Override
        public Builder clear() {
            super.clear();
            agencyId_ = "";
            bitField0_ = (bitField0_ & ~0x00000001);
            routeId_ = "";
            bitField0_ = (bitField0_ & ~0x00000002);
            routeType_ = 0;
            bitField0_ = (bitField0_ & ~0x00000004);
            if (tripBuilder_ == null) {
                trip_ = null;
            } else {
                tripBuilder_.clear();
            }
            bitField0_ = (bitField0_ & ~0x00000008);
            stopId_ = "";
            bitField0_ = (bitField0_ & ~0x00000010);
            return this;
        }
        
        @Override
        public Descriptor getDescriptorForType() {
            return GtfsRealtime.internal_static_transit_realtime_EntitySelector_descriptor;
        }
        
        @Override
        public EntitySelector getDefaultInstanceForType() {
            return getDefaultInstance();
        }
        
        @Override
        public EntitySelector build() {
            final EntitySelector result = buildPartial();
            if (!result.isInitialized()) {
                throw newUninitializedMessageException(result);
            }
            return result;
        }
        
        @Override
        public EntitySelector buildPartial() {
            final EntitySelector result = new EntitySelector(this);
            final int from_bitField0_ = bitField0_;
            int to_bitField0_ = 0;
            if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
                to_bitField0_ |= 0x00000001;
            }
            result.agencyId_ = agencyId_;
            if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
                to_bitField0_ |= 0x00000002;
            }
            result.routeId_ = routeId_;
            if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
                to_bitField0_ |= 0x00000004;
            }
            result.routeType_ = routeType_;
            if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
                to_bitField0_ |= 0x00000008;
            }
            if (tripBuilder_ == null) {
                result.trip_ = trip_;
            } else {
                result.trip_ = tripBuilder_.build();
            }
            if (((from_bitField0_ & 0x00000010) == 0x00000010)) {
                to_bitField0_ |= 0x00000010;
            }
            result.stopId_ = stopId_;
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
        public <Type> Builder setExtension(final GeneratedExtension<EntitySelector, Type> extension,
                final Type value) {
            return super.setExtension(extension, value);
        }
        
        @Override
        public <Type> Builder setExtension(
                final GeneratedExtension<EntitySelector, List<Type>> extension, final int index,
                final Type value) {
            return super.setExtension(extension, index, value);
        }
        
        @Override
        public <Type> Builder addExtension(
                final GeneratedExtension<EntitySelector, List<Type>> extension, final Type value) {
            return super.addExtension(extension, value);
        }
        
        @Override
        public <Type> Builder clearExtension(
                final GeneratedExtension<EntitySelector, ?> extension) {
            return super.clearExtension(extension);
        }
        
        @Override
        public Builder mergeFrom(final Message other) {
            if (other instanceof EntitySelector) {
                return mergeFrom((EntitySelector) other);
            } else {
                super.mergeFrom(other);
                return this;
            }
        }
        
        public Builder mergeFrom(final EntitySelector other) {
            if (other == getDefaultInstance()) {return this;}
            if (other.hasAgencyId()) {
                bitField0_ |= 0x00000001;
                agencyId_ = other.agencyId_;
                onChanged();
            }
            if (other.hasRouteId()) {
                bitField0_ |= 0x00000002;
                routeId_ = other.routeId_;
                onChanged();
            }
            if (other.hasRouteType()) {
                setRouteType(other.getRouteType());
            }
            if (other.hasTrip()) {
                mergeTrip(other.getTrip());
            }
            if (other.hasStopId()) {
                bitField0_ |= 0x00000010;
                stopId_ = other.stopId_;
                onChanged();
            }
            mergeExtensionFields(other);
            mergeUnknownFields(other.unknownFields);
            onChanged();
            return this;
        }
        
        @Override
        public final boolean isInitialized() {
            if (hasTrip()) {
                if (!getTrip().isInitialized()) {
                    return false;
                }
            }
            return extensionsAreInitialized();
        }
        
        @Override
        public Builder mergeFrom(final CodedInputStream input,
                final ExtensionRegistryLite extensionRegistry) throws IOException {
            EntitySelector parsedMessage = null;
            try {
                parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
            } catch (final InvalidProtocolBufferException e) {
                parsedMessage = (EntitySelector) e.getUnfinishedMessage();
                throw e.unwrapIOException();
            } finally {
                if (parsedMessage != null) {
                    mergeFrom(parsedMessage);
                }
            }
            return this;
        }
        
        private int bitField0_;
        
        @Setter(AccessLevel.PRIVATE) private Object agencyId_ = "";
        
        /**
         * <pre>
         * The values of the fields should correspond to the appropriate fields in the
         * GTFS feed.
         * At least one specifier must be given. If several are given, then the
         * matching has to apply to all the given specifiers.
         * </pre>
         * <p>
         * <code>optional string agency_id = 1;</code>
         */
        @Override
        public boolean hasAgencyId() {
            return ((bitField0_ & 0x00000001) == 0x00000001);
        }
        
        /**
         * <pre>
         * The values of the fields should correspond to the appropriate fields in the
         * GTFS feed.
         * At least one specifier must be given. If several are given, then the
         * matching has to apply to all the given specifiers.
         * </pre>
         * <p>
         * <code>optional string agency_id = 1;</code>
         */
        @Override
        public String getAgencyId() {
            return ProtoUtils.asString(agencyId_, this::setAgencyId_);
        }
        
        /**
         * <pre>
         * The values of the fields should correspond to the appropriate fields in the
         * GTFS feed.
         * At least one specifier must be given. If several are given, then the
         * matching has to apply to all the given specifiers.
         * </pre>
         * <p>
         * <code>optional string agency_id = 1;</code>
         */
        @Override
        public ByteString getAgencyIdBytes() {
            return ProtoUtils.asBytes(agencyId_, this::setAgencyId_);
        }
        
        /**
         * <pre>
         * The values of the fields should correspond to the appropriate fields in the
         * GTFS feed.
         * At least one specifier must be given. If several are given, then the
         * matching has to apply to all the given specifiers.
         * </pre>
         * <p>
         * <code>optional string agency_id = 1;</code>
         */
        public Builder setAgencyId(final String value) {
            Objects.requireNonNull(value);
            bitField0_ |= 0x00000001;
            agencyId_ = value;
            onChanged();
            return this;
        }
        
        /**
         * <pre>
         * The values of the fields should correspond to the appropriate fields in the
         * GTFS feed.
         * At least one specifier must be given. If several are given, then the
         * matching has to apply to all the given specifiers.
         * </pre>
         * <p>
         * <code>optional string agency_id = 1;</code>
         */
        public Builder clearAgencyId() {
            bitField0_ = (bitField0_ & ~0x00000001);
            agencyId_ = getDefaultInstance().getAgencyId();
            onChanged();
            return this;
        }
        
        /**
         * <pre>
         * The values of the fields should correspond to the appropriate fields in the
         * GTFS feed.
         * At least one specifier must be given. If several are given, then the
         * matching has to apply to all the given specifiers.
         * </pre>
         * <p>
         * <code>optional string agency_id = 1;</code>
         */
        public Builder setAgencyIdBytes(final ByteString value) {
            Objects.requireNonNull(value);
            bitField0_ |= 0x00000001;
            agencyId_ = value;
            onChanged();
            return this;
        }
        
        @Setter(AccessLevel.PRIVATE) private Object routeId_ = "";
        
        /**
         * <code>optional string route_id = 2;</code>
         */
        @Override
        public boolean hasRouteId() {
            return ((bitField0_ & 0x00000002) == 0x00000002);
        }
        
        /**
         * <code>optional string route_id = 2;</code>
         */
        @Override
        public String getRouteId() {
            return ProtoUtils.asString(routeId_, this::setRouteId_);
        }
        
        /**
         * <code>optional string route_id = 2;</code>
         */
        @Override
        public ByteString getRouteIdBytes() {
            return ProtoUtils.asBytes(routeId_, this::setRouteId_);
        }
        
        /**
         * <code>optional string route_id = 2;</code>
         */
        public Builder setRouteId(final String value) {
            Objects.requireNonNull(value);
            bitField0_ |= 0x00000002;
            routeId_ = value;
            onChanged();
            return this;
        }
        
        /**
         * <code>optional string route_id = 2;</code>
         */
        public Builder clearRouteId() {
            bitField0_ = (bitField0_ & ~0x00000002);
            routeId_ = getDefaultInstance().getRouteId();
            onChanged();
            return this;
        }
        
        /**
         * <code>optional string route_id = 2;</code>
         */
        public Builder setRouteIdBytes(final ByteString value) {
            Objects.requireNonNull(value);
            bitField0_ |= 0x00000002;
            routeId_ = value;
            onChanged();
            return this;
        }
        
        private int routeType_;
        
        /**
         * <pre>
         * corresponds to route_type in GTFS.
         * </pre>
         * <p>
         * <code>optional int32 route_type = 3;</code>
         */
        @Override
        public boolean hasRouteType() {
            return ((bitField0_ & 0x00000004) == 0x00000004);
        }
        
        /**
         * <pre>
         * corresponds to route_type in GTFS.
         * </pre>
         * <p>
         * <code>optional int32 route_type = 3;</code>
         */
        @Override
        public int getRouteType() {
            return routeType_;
        }
        
        /**
         * <pre>
         * corresponds to route_type in GTFS.
         * </pre>
         * <p>
         * <code>optional int32 route_type = 3;</code>
         */
        public Builder setRouteType(final int value) {
            bitField0_ |= 0x00000004;
            routeType_ = value;
            onChanged();
            return this;
        }
        
        /**
         * <pre>
         * corresponds to route_type in GTFS.
         * </pre>
         * <p>
         * <code>optional int32 route_type = 3;</code>
         */
        public Builder clearRouteType() {
            bitField0_ = (bitField0_ & ~0x00000004);
            routeType_ = 0;
            onChanged();
            return this;
        }
        
        private TripDescriptor trip_ = null;
        private SingleFieldBuilderV3<TripDescriptor, TripDescriptor.Builder,
                TripDescriptorOrBuilder>
                tripBuilder_;
        
        /**
         * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
         */
        @Override
        public boolean hasTrip() {
            return ((bitField0_ & 0x00000008) == 0x00000008);
        }
        
        /**
         * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
         */
        @Override
        public TripDescriptor getTrip() {
            if (tripBuilder_ == null) {
                return trip_ == null ? TripDescriptor.getDefaultInstance() : trip_;
            } else {
                return tripBuilder_.getMessage();
            }
        }
        
        /**
         * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
         */
        public Builder setTrip(final TripDescriptor value) {
            if (tripBuilder_ == null) {
                Objects.requireNonNull(value);
                trip_ = value;
                onChanged();
            } else {
                tripBuilder_.setMessage(value);
            }
            bitField0_ |= 0x00000008;
            return this;
        }
        
        /**
         * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
         */
        public Builder setTrip(final TripDescriptor.Builder builderForValue) {
            if (tripBuilder_ == null) {
                trip_ = builderForValue.build();
                onChanged();
            } else {
                tripBuilder_.setMessage(builderForValue.build());
            }
            bitField0_ |= 0x00000008;
            return this;
        }
        
        /**
         * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
         */
        public Builder mergeTrip(final TripDescriptor value) {
            if (tripBuilder_ == null) {
                if (((bitField0_ & 0x00000008) == 0x00000008) && trip_ != null
                        && trip_ != TripDescriptor.getDefaultInstance()) {
                    trip_ = TripDescriptor.newBuilder(trip_).mergeFrom(value).buildPartial();
                } else {
                    trip_ = value;
                }
                onChanged();
            } else {
                tripBuilder_.mergeFrom(value);
            }
            bitField0_ |= 0x00000008;
            return this;
        }
        
        /**
         * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
         */
        public Builder clearTrip() {
            if (tripBuilder_ == null) {
                trip_ = null;
                onChanged();
            } else {
                tripBuilder_.clear();
            }
            bitField0_ = (bitField0_ & ~0x00000008);
            return this;
        }
        
        /**
         * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
         */
        public TripDescriptor.Builder getTripBuilder() {
            bitField0_ |= 0x00000008;
            onChanged();
            return getTripFieldBuilder().getBuilder();
        }
        
        /**
         * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
         */
        @Override
        public TripDescriptorOrBuilder getTripOrBuilder() {
            if (tripBuilder_ != null) {
                return tripBuilder_.getMessageOrBuilder();
            } else {
                return trip_ == null ? TripDescriptor.getDefaultInstance() : trip_;
            }
        }
        
        /**
         * <code>optional .transit_realtime.TripDescriptor trip = 4;</code>
         */
        private SingleFieldBuilderV3<TripDescriptor, TripDescriptor.Builder,
                TripDescriptorOrBuilder> getTripFieldBuilder() {
            if (tripBuilder_ == null) {
                tripBuilder_ =
                        new SingleFieldBuilderV3<>(getTrip(), getParentForChildren(), isClean());
                trip_ = null;
            }
            return tripBuilder_;
        }
        
        @Setter(AccessLevel.PRIVATE) private Object stopId_ = "";
        
        /**
         * <code>optional string stop_id = 5;</code>
         */
        @Override
        public boolean hasStopId() {
            return ((bitField0_ & 0x00000010) == 0x00000010);
        }
        
        /**
         * <code>optional string stop_id = 5;</code>
         */
        @Override
        public String getStopId() {
            return ProtoUtils.asString(stopId_, this::setStopId_);
        }
        
        /**
         * <code>optional string stop_id = 5;</code>
         */
        @Override
        public ByteString getStopIdBytes() {
            return ProtoUtils.asBytes(stopId_, this::setStopId_);
        }
        
        /**
         * <code>optional string stop_id = 5;</code>
         */
        public Builder setStopId(final String value) {
            Objects.requireNonNull(value);
            bitField0_ |= 0x00000010;
            stopId_ = value;
            onChanged();
            return this;
        }
        
        /**
         * <code>optional string stop_id = 5;</code>
         */
        public Builder clearStopId() {
            bitField0_ = (bitField0_ & ~0x00000010);
            stopId_ = getDefaultInstance().getStopId();
            onChanged();
            return this;
        }
        
        /**
         * <code>optional string stop_id = 5;</code>
         */
        public Builder setStopIdBytes(final ByteString value) {
            Objects.requireNonNull(value);
            bitField0_ |= 0x00000010;
            stopId_ = value;
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
        
    }
    
    // @@protoc_insertion_point(class_scope:transit_realtime.EntitySelector)
    private static final EntitySelector DEFAULT_INSTANCE;
    
    static {
        DEFAULT_INSTANCE = new EntitySelector();
    }
    
    public static EntitySelector getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }
    
    @Deprecated public static final Parser<EntitySelector> PARSER = new AbstractParser<>() {
        
        @Override
        public EntitySelector parsePartialFrom(final CodedInputStream input,
                final ExtensionRegistryLite extensionRegistry)
                throws InvalidProtocolBufferException {
            return new EntitySelector(input, extensionRegistry);
        }
    };
    
    public static Parser<EntitySelector> parser() {
        return PARSER;
    }
    
    @Override
    public Parser<EntitySelector> getParserForType() {
        return PARSER;
    }
    
    @Override
    public EntitySelector getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }
    
}