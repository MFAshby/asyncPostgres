package com.ashbysoft.asyncdb.postgres.messages;

import com.ashbysoft.asyncdb.postgres.PostgresUtils;

import java.nio.ByteBuffer;

public class CommandComplete extends PostgresMessage {
    public static final char IDENTIFIER = 'C';

    private String commandTag = null;

    public CommandComplete() {
        super(IDENTIFIER);
    }

    @Override
    protected int getLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writePayloadToBuffer(ByteBuffer bb) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void readPayloadFromBuffer(ByteBuffer payloadBuffer) {
        commandTag = PostgresUtils.nullTerminated(payloadBuffer);
//        The command tag. This is usually a single word that identifies which SQL command was completed.
//
//                For an INSERT command, the tag is INSERT oid rows, where rows is the number of rows inserted. oid is the object ID of the inserted row if rows is 1 and the target table has OIDs; otherwise oid is 0.
//
//        For a DELETE command, the tag is DELETE rows where rows is the number of rows deleted.
//
//        For an UPDATE command, the tag is UPDATE rows where rows is the number of rows updated.
//
//        For a SELECT or CREATE TABLE AS command, the tag is SELECT rows where rows is the number of rows retrieved.
//
//        For a MOVE command, the tag is MOVE rows where rows is the number of rows the cursor's position has been changed by.
//
//        For a FETCH command, the tag is FETCH rows where rows is the number of rows that have been retrieved from the cursor.
//
//        For a COPY command, the tag is COPY rows where rows is the number of rows copied. (Note: the row count appears only in PostgreSQL 8.2 and later.)
    }

    public String getCommandTag() {
        return commandTag;
    }
}
