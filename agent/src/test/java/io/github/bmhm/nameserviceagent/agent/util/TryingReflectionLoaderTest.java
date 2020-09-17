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

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Optional;

class TryingReflectionLoaderTest {

  @Test
  void testReflectionClassLoaderDoesNotThrow() {
    final Optional<Class<?>> aClass = TryingReflectionLoader.loadFirstAvailableClass("a.b", "c.d");

    assertFalse(aClass.isPresent());
  }

  @Test
  void testReflectionFieldLoaderDoesNotThrow() {
    final Optional<Field> foundField = TryingReflectionLoader.loadFirstAvailableField(TryingReflectionLoaderTest.class, "not", "there");

    assertFalse(foundField.isPresent());
  }

}
