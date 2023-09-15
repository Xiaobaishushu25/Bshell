/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xbss.myterminal.intellij.util.containers;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class SingletonIteratorBase<T> implements Iterator<T> {
  private boolean myVisited;

  @Override
  public final boolean hasNext() {
    return !myVisited;
  }

  @Override
  public final T next() {
    if (myVisited) {
      throw new NoSuchElementException();
    }
    myVisited = true;
    checkCoModification();
    return getElement();
  }

  protected abstract void checkCoModification();

  protected abstract T getElement();
}