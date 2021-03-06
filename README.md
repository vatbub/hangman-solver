# hangman-solver
This is an algorithm to solve the popular [hangman game](https://en.wikipedia.org/wiki/Hangman_(game)).

## Download the standalone app
Get the FOKLauncher ([Download](https://bintray.com/vatbub/fokprojectsReleases/foklauncher#downloads)|[GitHub Repo](https://github.com/vatbub/fokLauncher)) and download "Hangman Solver" there.

## How the algorithm works
The algorithm is based on a dictionary taken from [Wiktionary](https://www.wiktionary.org/), the [Common Locale Data Repository](http://cldr.unicode.org/) and [various other WordNet projects](http://compling.hss.ntu.edu.sg/omw/).
  1. At first, the algorithm will compare the letter sequence it already guessed correctly (e. g. `_n_` if the word is `and` and `n` was already guessed) to all words in the dictionary with the same length.
  2. The word with the greatest amount of common letters is called the 'priority word' as the player most probably wants the computer to guess this word.
  3. If at least 70% of the priority word are equal to the letter sequence, the algorithm will output the whole priority word as his next guess. Otherwise, it will proceed with step 4.
  4. Now, the algorithm counts the letters in all words in the dictionary with the same length as the letter sequence and produces a ranking of most used letters. (This step is completely independent from step 2)
  5. The ranking now gets filtered so that it only contains the characters that were not yet guessed and are contained in the priority word.
  6. The next guess is the letter at the highest position in the filtered ranking.
  7. Letters or words that have been proven to be wrong are temporarily removed from the dictionary.
  
## How the heck can your algorithm play hangman in esperanto (and in 900 other languages)?
As the algorithms knowledge only depends on dictionaries, it is able to play hangman in almost any language that exists, including esperanto. Although we support 901 different languages, there are some languages that have better results than others. This mostly depends on the number of words in the corresponding dictionary. The more words the dictionary contains the better.

## The social experiment
One evening when we were testing the algorithm, we had the idea to launch a social experiment using this algorithm. Here is the Plan:
  - We will release a version of the algorithm for PC and Android
  - Next, we will collect what words are the most used in hangman
  - Which will give us a better dictionary
  - And most importantly a good overview about what humans are thinking about most of the time.
  
## Build the current snapshot
1. Clone this repository
2. Run `mvn package`

## Build the latest release
Repeat the steps mentioned above but switch to the `release` branch by running `git checkout release` prior to runing `mvn package`.

##Docs
[Maven Site](http://vatbubmvnsites.s3-website-us-west-2.amazonaws.com/hangmanSolver/0.0.18-SNAPSHOT/site/hangmanSolver/), [JavaDoc](http://vatbubmvnsites.s3-website-us-west-2.amazonaws.com/hangmanSolver/0.0.18-SNAPSHOT/site/hangmanSolver/apidocs/index.html)

#Current build status
[![Travis build status](https://travis-ci.org/vatbub/hangman-solver.svg?branch=master)](https://travis-ci.org/vatbub/hangman-solver/)

## Contributing
Contributions of any kind are very welcome. Just fork and submit a Pull Request and we will be happy to merge. Just keep in mind that we use [Issue driven development](https://github.com/vatbub/defaultRepo/wiki/Issue-driven-development).
