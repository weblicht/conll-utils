// Copyright 2008, 2009, 2013 Daniel de Kok
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

package de.tuebingen.uni.sfs.clarind.conllutils.readers;

import com.google.common.base.Optional;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for Brown-style corpora that are stored in a single file.
 */
public class BrownCorpusReader implements CorpusReader {
    private final BufferedReader reader;

    /**
     * Construct a Brown-style reader.
     */
    public BrownCorpusReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public Sentence readSentence() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty())
                continue;

            List<CONLLToken> tokens = new ArrayList<>();

            String[] lineParts = line.split("\\s+");
            for (int i = 0; i < lineParts.length; ++i) {
                String wordTag = lineParts[i];

                // Get the word and tag.
                int sepIndex = wordTag.lastIndexOf('/');

                if (sepIndex == -1)
                    throw new IOException(String.format("Tag is missing in '%s'", wordTag));

                String word = wordTag.substring(0, sepIndex);
                String tag = wordTag.substring(sepIndex + 1);

                if (word.isEmpty())
                    throw new IOException(String.format("Zero-length word in '%s'", wordTag));

                if (tag.isEmpty())
                    throw new IOException(String.format("Zero-length tag in '%s'", wordTag));

                tokens.add(new CONLLToken(i + 1, word, Optional.<String>absent(), Optional.of(tag), Optional.of(tag),
                        Optional.<String>absent(), Optional.<Integer>absent(), Optional.<String>absent(),
                        Optional.<Integer>absent(), Optional.<String>absent()));
            }

            return new PlainSentence(tokens);
        }

        return null;
    }
}
