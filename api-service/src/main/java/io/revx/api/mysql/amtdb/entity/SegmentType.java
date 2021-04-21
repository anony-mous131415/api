package io.revx.api.mysql.amtdb.entity;

public enum SegmentType
{
    CLICKER(1),
    HASH_BUCKET(2),
    DMP(3),
    PLATFORM(4);
  
    public final Integer id;

    private SegmentType(int id)
    {
        this.id = id;
    }

    public static SegmentType getById(Integer id)
    {
        for (SegmentType type : values()) {
            if (type.id.equals(id))
                return type;
        }
        return null;
    }
}
