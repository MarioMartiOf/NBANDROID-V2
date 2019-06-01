/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.nbandroid.netbeans.gradle.v2.color.preview;

import com.junichi11.netbeans.modules.color.codes.preview.api.OffsetRange;
import com.junichi11.netbeans.modules.color.codes.preview.spi.AbstractColorValue;
import com.junichi11.netbeans.modules.color.codes.preview.spi.ColorCodeFormatter;
import java.awt.Color;
import org.nbandroid.netbeans.gradle.v2.layout.values.completion.BasicColorValuesCompletionItem;

/**
 *
 * @author arsi
 */
public class XmlColorValue extends AbstractColorValue implements ColorCodeFormatter{

    private final Color color;

    public XmlColorValue(Color color, String value, OffsetRange offsetRange, int line) {
        super(value, offsetRange, line);
        this.color = color;
    }

    

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public ColorCodeFormatter getFormatter() {
        return this;
    }

    @Override
    public String format(Color color) {
        return BasicColorValuesCompletionItem.getHTMLColorString(color);
    }

}
