package io.revx.core.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum DataSourceType {

  PIXEL_LOG(1),

  AUDIENCE_FEED(2),

  FILE_UPLOAD(3);

  public final Integer id;

  private DataSourceType(int id) {
    this.id = id;
  }

  public static DataSourceType getById(Integer id) {
    for (DataSourceType type : values()) {
      if (type.id.equals(id))
        return type;
    }
    return null;
  }

  private static final List<DataSourceType> VALUES =
      Collections.unmodifiableList(Arrays.asList(values()));
  private static final int SIZE = VALUES.size();
  private static final Random RANDOM = new Random();

  public static DataSourceType randomDataSourceType() {
    return VALUES.get(RANDOM.nextInt(SIZE));
  }

}
