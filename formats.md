Notes on parsing each document, and on how they could possibly be analysed.

By Liam O Lionaird.

# FBIS

Just a collection of standalone documents.

* `<DOC> </DOC>` -> document
  * `<DOCNO> </DOCNO>` -> document ID
  * `<TI> </TI>` -> title
  * `<H4> </H4>` -> sub-title (not always there, but could be appended to title if it's there)
  * `<TEXT> </TEXT>` -> document text
    * First 3 lines contain article language/type - can be ignored
    * Then another subtitle in square brackets (starts with `<F P=106>`) - could include with title somehow
    * Text begins with `[Text]`
    * In interviews, each speaker is marked in square brackets - maybe process these as separate fields?
* Document ends with `</TEXT> </DOC>`

# FR94

Each text file contains a tree structure of documents, with an initial 'root' document and later documents branching from it / each other, identified with `<PARENT>` tags.

* *File structure:*
  * `*.0` -> 'Rules and Regulations'
  * `*.1` -> 'Proposed Rules'
  * `*.2` -> 'Notices'
* `<DOC> </DOC>` -> document
  * `<DOCNO> </DOCNO>` -> document ID
  * `<PARENT> </PARENT>` -> parent document
    * This links it to other documents in the dataset. Include under them too?
  * `<TEXT> </TEXT>` -> document text
    * First document in file (the root) always has no tags, probably no need to process
    * Just remove every `<!-- ... -->` tag, no need for them
    * Replace every `&blank;` with a space
    * `<DOCTITLE> </DOCTITLE>` -> document title
    * `<AGENCY> </AGENCY>` -> related government agency
    * `<SUMMARY> </SUMMARY>` -> document summary
    * `<SUPPLEM> </SUPPLEM>` -> supplementary information
* Document ends with `</TEXT> </DOC>`

# FT

Collection of standalone documents.

* `<DOC> </DOC>` -> document
  * `<DOCNO> </DOCNO>` -> document ID
  * `<HEADLINE> </HEADLINE>` -> article headline
    * Formatted as `FT  DD MMM YY / Topic: Title - Subtitle` (could trim first 16 chars)
  * `<TEXT> </TEXT>` - document text
  * No need to process `<PUB>` or `<PAGE>` tags
* Document ends with `</TEXT> </DOC>`

# LATIMES

Collection of standalone documents.

* `<DOC> </DOC>` -> document
  * `<DOCNO> </DOCNO>` -> document ID
  * Can remove any `<P>` and `</P>` tags first
  * `<SECTION> `
  * `<TYPE> </TYPE>` -> document type
    * e.g. Letter to the Editor, Art Review, Column, etc
    * Place more importance on certain types?
* Document ends with `</TEXT> </DOC>`
