"use strict";

const operation = func => (...operands) => (...vars) => func(...operands.map(x => x(...vars)))
const cnst = a => _ => a
const variable = variable => (...vars) => vars[variables.indexOf(variable)]
const add = operation((a, b) => a + b)
const subtract = operation((a, b) => a - b)
const multiply = operation((a, b) => a * b)
const divide = operation((a, b) => a / b)
const negate = operation(a => -a)
const one = cnst(1)
const two = cnst(2)
const min5 = operation(Math.min)
const max3 = operation(Math.max)

const variables = ['x', 'y', 'z']

const op = (argsCnt, f) => ({argsCnt, f})

const operations = new Map([
    ['+', op(2, add)],
    ['-', op(2, subtract)],
    ['*', op(2, multiply)],
    ['/', op(2, divide)],
    ['negate', op(1, negate)],
    ['one', op(0, _ => one)],
    ['two', op(0, _ => two)],
    ['min5', op(5, min5)],
    ['max3', op(3, max3)]
])

const parse = expression => {
    let stack = []
    for (const el of expression.trim().split(/\s+/)) {
        if (isFinite(el)) {
            stack.push(cnst(parseFloat(el)))
        } else if (variables.includes(el)) {
            stack.push(variable(el))
        } else if (operations.has(el)) {
            const operands = stack.splice(stack.length - operations.get(el).argsCnt)
            stack.push(operations.get(el).f(...operands))
        }
    }
    return stack[0];
}


for (let i = 0; i <= 10; i++) {
    console.log(add(
        subtract(
            multiply(
                variable("x"),
                variable("x")
            ),
            multiply(
                cnst(2),
                variable("x")
            )
        ),
        cnst(1))(i))
}

console.log(parse("-2 negate")())

/*
console.log(subtract(
    multiply(
        cnst(2),
        variable("x")
    ),
    cnst(3))(5)
)
 */