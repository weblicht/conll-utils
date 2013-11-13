package de.tuebingen.uni.sfs.clarind.conllutils.readers;

// Copyright 2008, 2013 Daniel de Kok
//
// This class was borrowed from the Jitar tagger.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CONLLReader implements CorpusReader {
    private final BufferedReader reader;
    private final List<CONLLToken> startMarkers;
    private final List<CONLLToken> endMarkers;
    boolean decapitalizeFirstWord;

    /**
     * Construct a CONLL corpus reader without start/end tokens and first word decapitalization.
     * @param reader A corpus reader.
     */
    public CONLLReader(BufferedReader reader) {
        this(reader, ImmutableList.<CONLLToken>of(), ImmutableList.<CONLLToken>of(), false);
    }

    public CONLLReader(BufferedReader reader, List<CONLLToken> startMarkers, List<CONLLToken> endMarkers, boolean decapitalizeFirstWord) {
        this.reader = reader;
        this.startMarkers = startMarkers;
        this.endMarkers = endMarkers;
        this.decapitalizeFirstWord = decapitalizeFirstWord;
    }

    private static String replaceCharAt(String str, int pos, char c) {
        StringBuilder sb = new StringBuilder();
        sb.append(str.substring(0, pos));
        sb.append(c);
        sb.append(str.substring(pos + 1));
        return sb.toString();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public List<CONLLToken> readSentence() throws IOException {
        List<CONLLToken> sentence = new ArrayList<>(startMarkers);

        String line;
        while ((line = reader.readLine()) != null) {
            String parts[] = StringUtils.split(line.trim(), '\t');

            // We are done with this sentence.
            if (parts.length == 0) {
                sentence.addAll(endMarkers);
                return sentence;
            }

            if (parts.length < 2)
                throw new IOException(String.format("Line has fewer than two columns: %s", line));

            String form = parts[1];

            if (decapitalizeFirstWord && sentence.size() == startMarkers.size())
                form = replaceCharAt(form, 0, Character.toLowerCase(form.charAt(0)));

            Integer tokenId = Integer.parseInt(parts[0]);
            Optional<String> lemma = valueForColumn(parts, 2);
            Optional<String> courseTag = valueForColumn(parts, 3);
            Optional<String> tag = valueForColumn(parts, 4);
            Optional<String> features = valueForColumn(parts, 5);
            Optional<Integer> head = intValueForColumn(parts, 6);
            Optional<String> headRel = valueForColumn(parts, 7);
            Optional<Integer> pHead = intValueForColumn(parts, 8);
            Optional<String> pHeadRel = valueForColumn(parts, 9);

            CONLLToken conllToken = new CONLLToken(tokenId, form, lemma, courseTag, tag, features, head, headRel,
                    pHead, pHeadRel);

            sentence.add(conllToken);
        }

        // If the the file does not end with a blank line, we have left-overs.
        if (sentence.size() != startMarkers.size()) {
            sentence.addAll(endMarkers);
            return sentence;
        }

        return null;
    }

    private Optional<String> valueForColumn(String[] columns, int column) {
        if (column >= columns.length)
            return Optional.absent();

        if (columns[column].equals("_"))
            return Optional.absent();

        return Optional.of(columns[column]);
    }

    private Optional<Integer> intValueForColumn(String[] columns, int column) {
        if (column >= columns.length)
            return Optional.absent();

        if (columns[column].equals("_"))
            return Optional.absent();

        return Optional.of(Integer.parseInt(columns[column]));
    }

}
