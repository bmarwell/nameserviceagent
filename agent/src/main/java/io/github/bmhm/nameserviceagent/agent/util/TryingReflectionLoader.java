/*
 * Copyright 2020-2020 the nameserviceangent team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.bmhm.nameserviceagent.agent.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class TryingReflectionLoader {

  private static final Logger LOG = Logger.getLogger(TryingReflectionLoader.class.getCanonicalName());

  private TryingReflectionLoader() {
    // util
  }

  public static Optional<Class<?>> loadFirstAvailableClass(final String... className) {
    final Stream<Class<?>> classStream = Arrays.stream(className)
        .map(TryingReflectionLoader::tryLoadClass);

    /*
     * map can be shitty sometimes… you cannot method chain this,
     * b/c the java compiler thinks tryLoad will return Class<? extends Class<?>>.
     * However, assigning to Class<?> as above works…
     */

    return classStream
        .filter(Objects::nonNull)
        .findFirst();
  }

  protected static Class<?> tryLoadClass(final String className) {
    try {
      return Class.forName(className);
    } catch (final ClassNotFoundException javaLangClassNotFoundException) {
      return null;
    }
  }

  public static Optional<Field> loadFirstAvailableField(final Class<?> clazz,
                                                        final String... fieldNames) {
    final Stream<Field> fields = Arrays.stream(fieldNames)
        .map(fieldName -> tryLoadField(clazz, fieldName));

    return fields
        .filter(Objects::nonNull)
        .findFirst();
  }

  private static Field tryLoadField(final Class<?> clazz, final String fieldName) {
    try {
      LOG.log(Level.FINE, "Trying to load [" + clazz.getCanonicalName() + "::" + fieldName + "].");
      return clazz.getDeclaredField(fieldName);
    } catch (final NoSuchFieldException noSuchFieldException) {
      LOG.log(
          Level.FINE,
          noSuchFieldException,
          () -> String.format(Locale.ENGLISH, "Failed to load [%s::%s].", clazz.getCanonicalName(), fieldName));
      return null;
    }
  }
}
