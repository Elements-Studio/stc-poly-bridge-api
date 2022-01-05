package org.starcoin.polybridge.utils;

import com.novi.bcs.BcsSerializer;
import com.novi.serde.Bytes;
import com.novi.serde.Int128;
import com.novi.serde.SerializationError;
import com.novi.serde.Unsigned;
import org.starcoin.types.AccountAddress;
import org.starcoin.types.TransactionArgument;

import java.math.BigInteger;

public class StarcoinTransactionPayloadUtils {


    private static Bytes encode_u8_argument(@Unsigned Byte arg) {
        try {

            BcsSerializer s = new BcsSerializer();
            s.serialize_u8(arg);
            return Bytes.valueOf(s.get_bytes());

        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u8");
        }
    }


    private static Bytes encode_u64_argument(@Unsigned Long arg) {
        try {

            BcsSerializer s = new BcsSerializer();
            s.serialize_u64(arg);
            return Bytes.valueOf(s.get_bytes());

        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u64");
        }
    }

    private static Bytes encode_u128_argument(@Unsigned @Int128 BigInteger arg) {
        try {

            BcsSerializer s = new BcsSerializer();
            s.serialize_u128(arg);
            return Bytes.valueOf(s.get_bytes());

        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u128");
        }
    }

    private static Bytes encode_address_argument(AccountAddress arg) {
        try {

            return Bytes.valueOf(arg.bcsSerialize());

        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type address");
        }
    }

    private static Bytes encode_u8vector_argument(Bytes arg) {
        try {
            BcsSerializer s = new BcsSerializer();
            s.serialize_bytes(arg);
            return Bytes.valueOf(s.get_bytes());

        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u8vector");
        }
    }

    private static Boolean decode_bool_argument(TransactionArgument arg) {
        if (!(arg instanceof TransactionArgument.Bool)) {
            throw new IllegalArgumentException("Was expecting a Bool argument");
        }
        return ((TransactionArgument.Bool) arg).value;
    }

    private static @Unsigned Byte decode_u8_argument(TransactionArgument arg) {
        if (!(arg instanceof TransactionArgument.U8)) {
            throw new IllegalArgumentException("Was expecting a U8 argument");
        }
        return ((TransactionArgument.U8) arg).value;
    }

}
