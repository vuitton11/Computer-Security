import sys
import os
import string
from operator import itemgetter

def freq_anal(text):
    """
    Performs frequency analysis on the known text and sorts them from least to greatest
    :param text: input Text
    :return: A list of the sorted frequency analysis
    """
    letters = list(string.ascii_lowercase)
    letters.append(" ")
    #print(letters)
    #print(len(letters))
    analysis = {}
    for x in letters:
        analysis[x] = text.count(x)

    final_anal = {k: v / total for total in (sum(analysis.values()),) for k, v in analysis.items()}
    final_sort = sorted(final_anal.items(), key=itemgetter(1))

    return final_sort

def Decipher(text, cipher, known):
    """
    Performs the Monoalphabetic Cipher by:
    Replacing all the letters the text using a master dict
    :param text: the text that is going to be decrypted
    :param cipher: key item of the frequent analysis of cipher text
    :param known: key item of the frequent analysis of known text
    :return: returns the decipher text
    """
    output = ''
    #design a new dict
    master_key = dict(zip(cipher, known))
    for i in text:
        if(i in master_key):
            output += master_key[i]
        else:
            output += i
    return output


def main():
    cipher_file = sys.argv[1]
    known_file = sys.argv[2]

    exists = os.path.isfile(cipher_file)
    exists2 = os.path.isfile(known_file)
    if exists and exists2:
        # Store configuration file values
        file1 = open(known_file, "r+")
        file2 = open(cipher_file, "r+")

        known_text = file1.read()
        file1.close()

        cipher_text = file2.read()
        file2.close()

        known_anal = freq_anal(known_text.lower())
        #print(known_anal)

        cipher_anal = freq_anal(cipher_text.lower())
        #print(cipher_anal)

        #used to get keys from a 2d list
        #[i[0] for i in a]

        output = Decipher(cipher_text.lower(), [i[0] for i in cipher_anal], [i[0] for i in known_anal]).encode("ascii", "ignore")
        print((output).decode('utf8'))

    else:
        print("Could not find files")

if __name__ == "__main__":
    main()