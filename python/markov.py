#!/usr/bin/env python3
import random

def start_string = "|START|"

def wordgenerator(stream):
"""Yields one word at a time from a stream. Strips newlines and the utf8 byte order mark"""
    yield start_string
    for line in stream.readlines():
        for word in line.split(" "):
            yield word.translate({ord("\n"): None, ord("\ufeff"): None})

def train(stream, word_hash, depth):
"""Creates a nested "word hash" of words which follow each other, and their count at leaf nodes."""
    words = []
    for word in wordgenerator(stream):
        words.append(word)
        if len(words) < depth:
            continue
        else:
            update_hash(word_hash, words)
            words.pop(0)
            

def update_hash(word_hash, words):
"""Takes a list of words and increments or creates their leaf node in a word hash"""
    sub_hash = word_hash
    for word in words[:-1]:
        if word not in sub_hash.keys():
            sub_hash[word] = dict()
        sub_hash = sub_hash[word]
    if words[-1] in sub_hash.keys():
        sub_hash[words[-1]] += 1
    else:
        sub_hash[words[-1]] = 1

def get_weight(item):
"""Given a node in a word hash, returns its weight (or the sum of weights of its children)"""
    while True:
        if isinstance(item, int):
            return item
        else:
            return sum([get_weight(val) for key, val in item.items()])

def get_weighted_words(word_hash, words):
"""Gets a weighted list of word choices directly under the given words in the hash"""
    sub_hash = word_hash
    for word in words:
        if word in sub_hash.keys():
            sub_hash = sub_hash[word]
        else:
            return False
    return {word: get_weight(val) for word, val in sub_hash.items()}

def choose_weighted(weighted_words):
"""Chooses an entry at random from a weighted list"""
    total = sum([weight for word, weight in weighted_words.items()])
    i = random.randint(0, total - 1)
    for word, weight in weighted_words.items():
        if i < weight:
            return word
        else:
            i -= weight
    

def generate_word(word_hash, words):
"""Generates a single word given the current list of words from a word hash"""
    while len(words) > 0:
        weighted_words = get_weighted_words(word_hash, words)
        if weighted_words:
            break
        else:
            words = words[:-1]
    print(weighted_words, words)
    return choose_weighted(weighted_words)


def generate(word_hash, depth, length):
"""Generates a number of words from a word hash using a given depth"""
    words = [start_string]
    for i in range(length):
        words.append(generate_word(word_hash, words[-depth:]))
    words.pop(0)
    return words

