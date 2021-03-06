/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.template.soy.types.proto;

import com.google.common.base.CaseFormat;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;

/** Helper class for generating fully qualified JSPB identifiers for descriptors. */
class JsQualifiedNameHelper {

  /** Returns the JS name of the extension, suitable for passing to getExtension(). */
  static String getQualifiedExtensionName(FieldDescriptor field) {
    Descriptor scope = field.getExtensionScope();
    if (scope != null) {
      return getQualifiedName(scope) + "." + computeSoyName(field);
    }
    return Protos.getJsPackage(field.getFile()) + "." + computeSoyName(field);
  }

  /** Returns the JS name of the import for the given extension, suitable for goog.require. */
  static String getImportForExtension(FieldDescriptor field) {
    Descriptor scope = field.getExtensionScope();
    if (scope != null) {
      while (scope.getContainingType() != null) {
        scope = scope.getContainingType();
      }
      return getQualifiedName(scope);
    }
    return Protos.getJsPackage(field.getFile()) + "." + computeSoyName(field);
  }

  /** Returns the JS name of the given message type. */
  static String getQualifiedName(Descriptor descriptor) {
    String protoPackage = descriptor.getFile().getPackage();
    // We need a semi-qualified name: including containig types but not the package.
    String name = descriptor.getFullName();
    if (!name.startsWith(protoPackage)) {
      throw new AssertionError("Expected \"" + name + "\" to start with \"" + protoPackage + "\"");
    }
    return Protos.getJsPackage(descriptor.getFile()) + name.substring(protoPackage.length());
  }

  /** Performs camelcase translation. */
  private static String computeSoyName(FieldDescriptor field) {
    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, field.getName());
  }
}
